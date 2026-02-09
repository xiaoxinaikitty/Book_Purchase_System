package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Address;
import com.bookmanager.entity.Book;
import com.bookmanager.entity.Cart;
import com.bookmanager.entity.Order;
import com.bookmanager.entity.OrderItem;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.mapper.AddressMapper;
import com.bookmanager.mapper.CartMapper;
import com.bookmanager.mapper.OrderItemMapper;
import com.bookmanager.mapper.OrderMapper;
import com.bookmanager.service.BookService;
import com.bookmanager.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private BookService bookService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, List<Long> cartIds, Long addressId, String remark) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (cartIds == null || cartIds.isEmpty()) {
            throw new BusinessException("请选择要结算的商品");
        }

        Address address = addressMapper.selectById(addressId);
        if (address == null || !userId.equals(address.getUserId())) {
            throw new BusinessException("收货地址不存在");
        }

        List<Cart> cartList = cartMapper.selectCartListByUserId(userId);
        if (cartList == null || cartList.isEmpty()) {
            throw new BusinessException("购物车为空");
        }

        Set<Long> cartIdSet = new HashSet<>(cartIds);
        List<Cart> selected = new ArrayList<>();
        for (Cart cart : cartList) {
            if (cartIdSet.contains(cart.getId())) {
                selected.add(cart);
            }
        }

        if (selected.isEmpty()) {
            throw new BusinessException("请选择要结算的商品");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (Cart cart : selected) {
            Book book = cart.getBook();
            if (book == null || book.getStatus() == null) {
                book = bookService.getById(cart.getBookId());
            }
            if (book == null || book.getStatus() == null || book.getStatus() != 1) {
                throw new BusinessException("图书不存在或已下架");
            }
            Integer stock = book.getStock() == null ? 0 : book.getStock();
            if (stock < cart.getQuantity()) {
                throw new BusinessException("库存不足: " + book.getTitle());
            }

            BigDecimal price = book.getPrice() == null ? BigDecimal.ZERO : book.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cart.getQuantity()));
            total = total.add(subtotal);

            OrderItem item = new OrderItem();
            item.setBookId(book.getId());
            item.setBookTitle(book.getTitle());
            item.setBookPrice(price);
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(subtotal);
            items.add(item);
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setStatus(0);
        order.setRemark(remark);
        order.setReceiver(address.getReceiver());
        order.setPhone(address.getPhone());
        order.setAddress(buildFullAddress(address));

        boolean saved = this.save(order);
        if (!saved) {
            throw new BusinessException("创建订单失败");
        }

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
            bookService.updateStock(item.getBookId(), item.getQuantity());
        }

        LambdaQueryWrapper<Cart> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(Cart::getUserId, userId).in(Cart::getId, cartIds);
        cartMapper.delete(deleteWrapper);

        order.setOrderItems(items);
        return order;
    }

    @Override
    public IPage<Order> getUserOrderPage(Long userId, Integer page, Integer size, Integer status) {
        Page<Order> pageParam = new Page<>(page, size);
        return this.baseMapper.selectOrderPage(pageParam, userId, status);
    }

    @Override
    public Order getOrderDetail(Long orderId) {
        Order order = this.baseMapper.selectOrderById(orderId);
        if (order == null) {
            return null;
        }
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        order.setOrderItems(items);
        return order;
    }

    @Override
    public boolean cancelOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 0) {
            throw new BusinessException("当前订单无法取消");
        }
        Order update = new Order();
        update.setId(orderId);
        update.setStatus(4);
        return this.updateById(update);
    }

    @Override
    public boolean payOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 0) {
            throw new BusinessException("当前订单无法支付");
        }
        Order update = new Order();
        update.setId(orderId);
        update.setStatus(1);
        boolean updated = this.updateById(update);
        if (updated) {
            List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
            for (OrderItem item : items) {
                bookService.increaseSales(item.getBookId(), item.getQuantity());
            }
        }
        return updated;
    }

    @Override
    public IPage<Order> getAllOrderPage(Integer page, Integer size, Integer status, String orderNo) {
        Page<Order> pageParam = new Page<>(page, size);
        return this.baseMapper.selectAllOrderPage(pageParam, status, orderNo);
    }

    @Override
    public boolean shipOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 1) {
            throw new BusinessException("当前订单无法发货");
        }
        Order update = new Order();
        update.setId(orderId);
        update.setStatus(2);
        return this.updateById(update);
    }

    @Override
    public boolean confirmOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 2) {
            throw new BusinessException("当前订单无法确认收货");
        }
        Order update = new Order();
        update.setId(orderId);
        update.setStatus(3);
        return this.updateById(update);
    }

    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 9000) + 1000;
        return "ORD" + timePart + random;
    }

    private String buildFullAddress(Address address) {
        StringBuilder sb = new StringBuilder();
        if (address.getProvince() != null) {
            sb.append(address.getProvince());
        }
        if (address.getCity() != null) {
            sb.append(address.getCity());
        }
        if (address.getDistrict() != null) {
            sb.append(address.getDistrict());
        }
        if (address.getDetail() != null) {
            sb.append(address.getDetail());
        }
        return sb.toString();
    }
}

