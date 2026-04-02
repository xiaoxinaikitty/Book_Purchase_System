-- Supplemental real-content seed data for Book Purchase System
-- 作用:
-- 1. 替换用户端核心图书为真实封面
-- 2. 将测试图书 1/2/3 替换为真实图书，避免污染新书榜
-- 3. 补充评价、浏览、收藏、购物车、订单和公告数据

SET NAMES utf8mb4;

START TRANSACTION;

-- 真实图书与封面修正
INSERT INTO book (
    id, title, author, publisher, isbn, category_id, price, stock,
    cover_image, description, publish_date, sales, rating, status, create_time, update_time
) VALUES
(1, 'Clean Code', 'Robert C. Martin', 'Prentice Hall', '9780132350884', 91007, 89.00, 36,
 '/uploads/cover/seed-clean-code.jpg',
 '围绕命名、函数拆分、测试与重构展开的经典工程实践读物。',
 '2024-02-01', 860, 4.7, 1, '2026-04-02 09:10:00', '2026-04-02 09:10:00'),
(2, 'Spring in Action, Sixth Edition', 'Craig Walls', 'Manning', '9781617297571', 91007, 126.00, 42,
 '/uploads/cover/seed-spring-in-action-6.jpg',
 '覆盖 Spring 6 常见开发场景与现代化工程实践，适合 Web 后端项目实战。',
 '2024-03-15', 940, 4.7, 1, '2026-04-02 09:20:00', '2026-04-02 09:20:00'),
(3, 'Java核心技术 卷I（原书第12版）', 'Cay S. Horstmann', '机械工业出版社', '9787111708703', 91007, 149.00, 48,
 '/uploads/cover/seed-core-java-12.jpg',
 '以新版 Java 语法、集合、并发与面向对象基础为核心的入门经典。',
 '2024-01-12', 780, 4.8, 1, '2026-04-02 09:30:00', '2026-04-02 09:30:00'),
(92001, '时间简史', '史蒂芬·霍金', '湖南科技出版社', '9787535732309', 91004, 58.00, 120,
 '/uploads/cover/seed-brief-history-of-time.jpg',
 '从宇宙起源、黑洞到时间箭头，经典物理学科普读物兼具想象力与严谨性。',
 '2022-06-01', 980, 4.8, 1, '2026-03-31 14:00:00', '2026-03-31 14:00:00'),
(92002, '三体', '刘慈欣', '重庆出版社', '9787536692930', 91004, 79.00, 200,
 '/uploads/cover/seed-three-body.jpg',
 '以文明碰撞与宇宙尺度想象展开的中文科幻代表作，适合喜欢硬核设定的读者。',
 '2021-08-01', 1260, 4.9, 1, '2026-03-31 15:10:00', '2026-03-31 15:10:00'),
(92004, '活着', '余华', '作家出版社', '9787506365437', 91005, 45.00, 300,
 '/uploads/cover/seed-huozhe.jpg',
 '用极克制的语言书写命运、亲情与生命韧性，适合反复回味。',
 '2019-01-15', 1080, 4.9, 1, '2026-03-31 16:40:00', '2026-03-31 16:40:00'),
(92005, '平凡的世界', '路遥', '人民文学出版社', '9787020049295', 91005, 66.00, 180,
 '/uploads/cover/seed-ordinary-world.jpg',
 '在时代洪流中书写普通人的奋斗与情感，是中文现实主义长篇中的高口碑之作。',
 '2018-03-12', 960, 4.8, 1, '2026-03-31 18:20:00', '2026-03-31 18:20:00'),
(92006, 'Java核心技术 卷I', '凯·霍斯特曼', '机械工业出版社', '9787115542105', 91007, 128.00, 90,
 '/uploads/cover/seed-core-java-12.jpg',
 '覆盖 Java 语言基础、面向对象、集合和并发，是后端开发常备参考书。',
 '2023-05-10', 900, 4.7, 1, '2026-04-01 09:50:00', '2026-04-01 09:50:00'),
(92007, 'Spring实战', 'Craig Walls', '人民邮电出版社', '9787115599055', 91007, 98.00, 110,
 '/uploads/cover/seed-spring-in-action-6.jpg',
 '围绕 Spring Boot 与常见企业级开发流程展开，适合快速搭建实战知识体系。',
 '2024-01-05', 880, 4.6, 1, '2026-04-01 11:10:00', '2026-04-01 11:10:00'),
(92008, 'Vue.js设计与实现', '霍春阳', '人民邮电出版社', '9787115591233', 91006, 88.00, 140,
 '/uploads/cover/seed-vue-design.jpg',
 '从响应式、渲染器到编译器系统讲清 Vue 核心设计，适合深入框架原理。',
 '2023-07-20', 860, 4.8, 1, '2026-04-01 12:30:00', '2026-04-01 12:30:00'),
(92009, 'JavaScript高级程序设计', 'Nicholas C. Zakas', '人民邮电出版社', '9787115545380', 91006, 109.00, 160,
 '/uploads/cover/seed-js-advanced-4e.jpg',
 '前端工程师高频参考书，系统覆盖语言机制、DOM、异步和浏览器生态。',
 '2020-09-01', 1180, 4.8, 1, '2026-04-01 13:50:00', '2026-04-01 13:50:00'),
(92011, '指数基金投资指南', '螺丝钉', '中信出版社', '9787508697161', 91008, 72.00, 150,
 '/uploads/cover/seed-index-funds.jpg',
 '以长期定投和资产配置为主线，适合理财入门与建立纪律化投资习惯。',
 '2022-02-18', 820, 4.6, 1, '2026-04-01 15:10:00', '2026-04-01 15:10:00'),
(92012, '增长黑客', '肖恩·埃利斯', '人民邮电出版社', '9787115416297', 91009, 85.00, 170,
 '/uploads/cover/seed-growth-hacker.jpg',
 '聚焦用户增长、实验驱动和漏斗优化，适合产品、运营和创业团队阅读。',
 '2019-07-01', 790, 4.5, 1, '2026-04-01 16:30:00', '2026-04-01 16:30:00'),
(92014, '算法图解', 'Aditya Bhargava', '人民邮电出版社', '9787115474884', 91007, 79.00, 150,
 '/uploads/cover/seed-grokking-algorithms.jpg',
 '以图解方式讲解常见算法与复杂度概念，适合零基础到初阶程序员。',
 '2022-09-10', 760, 4.7, 1, '2026-04-01 17:50:00', '2026-04-01 17:50:00'),
(92016, '纳瓦尔宝典', '埃里克·乔根森', '中信出版社', '9787521720261', 91008, 64.00, 210,
 '/uploads/cover/seed-naval.jpg',
 '围绕财富积累、杠杆、判断力与幸福感，适合长期主义读者慢读。',
 '2021-03-12', 740, 4.7, 1, '2026-04-01 19:10:00', '2026-04-01 19:10:00')
ON DUPLICATE KEY UPDATE
title = VALUES(title),
author = VALUES(author),
publisher = VALUES(publisher),
isbn = VALUES(isbn),
category_id = VALUES(category_id),
price = VALUES(price),
stock = VALUES(stock),
cover_image = VALUES(cover_image),
description = VALUES(description),
publish_date = VALUES(publish_date),
sales = VALUES(sales),
rating = VALUES(rating),
status = VALUES(status),
create_time = VALUES(create_time),
update_time = VALUES(update_time);

-- 补充评价数据，并把原测试评价同步改成真实内容
INSERT INTO review (id, user_id, book_id, rating, content, create_time) VALUES
(1, 5, 1, 4, '命名和重构章节很实用，读完就能直接改自己的老代码。', '2026-02-09 19:18:36'),
(2, 6, 2, 5, 'Spring 项目实战覆盖很全，示例也比较贴近现在的工程写法。', '2026-02-09 19:26:02'),
(98101, 93002, 1, 5, '关于命名和函数拆分的建议非常落地，适合工程团队统一代码风格。', '2026-03-20 10:12:00'),
(98102, 93002, 92009, 5, '章节组织清晰，查闭包、原型和异步流程都很顺手。', '2026-03-20 10:18:00'),
(98103, 93003, 3, 5, '新版 Java 语法和集合部分讲得很扎实，适合系统复习。', '2026-03-21 14:05:00'),
(98104, 93003, 92007, 4, '适合做 Spring Boot 项目时边学边查，整体体系感不错。', '2026-03-21 14:18:00'),
(98105, 93004, 92002, 5, '宏大设定之外，人物选择也很有张力，越读越上头。', '2026-03-22 19:10:00'),
(98106, 93004, 92004, 5, '语言很克制，但情感后劲特别强。', '2026-03-22 19:30:00'),
(98107, 93005, 2, 4, '如果已经有基础，再读这一版会更容易把 Spring 体系串起来。', '2026-03-23 11:02:00'),
(98108, 93005, 92016, 5, '不是鸡汤，更像一套长期思考和选择的方法。', '2026-03-23 11:20:00'),
(98109, 93005, 3, 4, '基础知识覆盖全面，适合做 Java 工程师案头书。', '2026-03-23 11:35:00'),
(98110, 93006, 92014, 5, '图解方式很适合算法入门，复杂度也解释得直观。', '2026-03-24 09:45:00'),
(98111, 93006, 92001, 4, '把宇宙学的大问题讲得很有画面感，科普体验很好。', '2026-03-24 10:10:00'),
(98112, 93006, 92009, 5, '对闭包、原型和异步流程的梳理很见功力。', '2026-03-24 10:25:00'),
(98113, 93007, 92008, 4, '对响应式系统和编译器设计的解释尤其有帮助。', '2026-03-25 16:00:00'),
(98114, 93007, 1, 5, '很多示例都能映射到日常开发中的坏味道，实用性很高。', '2026-03-25 16:28:00')
ON DUPLICATE KEY UPDATE
user_id = VALUES(user_id),
book_id = VALUES(book_id),
rating = VALUES(rating),
content = VALUES(content),
create_time = VALUES(create_time);

-- 补充浏览记录
INSERT INTO browse_history (id, user_id, book_id, browse_count, last_browse_time) VALUES
(1, 5, 1, 3, '2026-03-18 21:06:00'),
(2, 5, 2, 4, '2026-03-26 20:10:00'),
(99014, 5, 3, 2, '2026-04-02 17:08:57'),
(99101, 93002, 1, 4, '2026-03-20 10:25:00'),
(99102, 93002, 92009, 5, '2026-03-20 10:32:00'),
(99103, 93003, 3, 4, '2026-03-21 14:26:00'),
(99104, 93003, 92007, 5, '2026-03-21 14:33:00'),
(99105, 93004, 92002, 4, '2026-03-22 19:45:00'),
(99106, 93004, 92004, 3, '2026-03-22 19:52:00'),
(99107, 93005, 2, 3, '2026-03-23 11:45:00'),
(99108, 93005, 92016, 5, '2026-03-23 11:50:00'),
(99109, 93006, 92014, 5, '2026-03-24 10:32:00'),
(99110, 93006, 92001, 3, '2026-03-24 10:40:00'),
(99111, 93007, 92008, 4, '2026-03-25 16:32:00'),
(99112, 93007, 1, 2, '2026-03-25 16:40:00')
ON DUPLICATE KEY UPDATE
user_id = VALUES(user_id),
book_id = VALUES(book_id),
browse_count = VALUES(browse_count),
last_browse_time = VALUES(last_browse_time);

-- 补充收藏
INSERT INTO favorite (id, user_id, book_id, create_time) VALUES
(1, 5, 2, '2026-02-09 20:15:16'),
(99601, 93002, 1, '2026-03-20 10:22:00'),
(99602, 93003, 3, '2026-03-21 14:22:00'),
(99603, 93004, 92002, '2026-03-22 19:40:00'),
(99604, 93005, 2, '2026-03-23 11:40:00'),
(99605, 93005, 92016, '2026-03-23 11:42:00'),
(99606, 93006, 92014, '2026-03-24 10:30:00'),
(99607, 93007, 1, '2026-03-25 16:30:00'),
(99608, 93007, 92008, '2026-03-25 16:35:00')
ON DUPLICATE KEY UPDATE
user_id = VALUES(user_id),
book_id = VALUES(book_id),
create_time = VALUES(create_time);

-- 补充购物车
INSERT INTO cart (id, user_id, book_id, quantity, create_time) VALUES
(1, 5, 1, 1, '2026-02-09 18:47:09'),
(97101, 93002, 1, 1, '2026-03-20 10:38:00'),
(97102, 93003, 3, 1, '2026-03-21 14:40:00'),
(97103, 93005, 2, 1, '2026-03-23 11:48:00'),
(97104, 93006, 92014, 1, '2026-03-24 10:42:00'),
(97105, 93007, 92004, 1, '2026-03-25 16:45:00')
ON DUPLICATE KEY UPDATE
user_id = VALUES(user_id),
book_id = VALUES(book_id),
quantity = VALUES(quantity),
create_time = VALUES(create_time);

-- 订单与订单明细
INSERT INTO `order` (
    id, order_no, user_id, total_amount, status, address, receiver, phone, remark, create_time, update_time
) VALUES
(1, 'ORD202602091856074175', 5, 126.00, 3, '安徽省合肥市包河区111111', 'xiaoxu', '13695516211', '真实图书示例订单', '2026-02-09 18:56:07', '2026-04-02 09:20:00'),
(95101, 'SEED20260320-95101', 93002, 198.00, 3, '浙江省杭州市西湖区文一路 99 号', 'Alice', '13900010001', '前端技术补货', '2026-03-20 11:10:00', '2026-03-21 09:00:00'),
(95102, 'SEED20260321-95102', 93003, 247.00, 2, '上海市浦东新区张江路 88 号', 'Bob', '13900010002', '后端学习书单', '2026-03-21 15:05:00', '2026-03-22 09:10:00'),
(95103, 'SEED20260323-95103', 93005, 190.00, 3, '广东省深圳市南山区科技园 168 号', 'David', '13900010004', '投资与技术混合购书', '2026-03-23 12:15:00', '2026-03-24 10:00:00'),
(95104, 'SEED20260324-95104', 93006, 137.00, 1, '四川省成都市高新区天府大道 520 号', 'Erin', '13900010005', '准备周末阅读', '2026-03-24 11:05:00', '2026-03-24 11:05:00')
ON DUPLICATE KEY UPDATE
order_no = VALUES(order_no),
user_id = VALUES(user_id),
total_amount = VALUES(total_amount),
status = VALUES(status),
address = VALUES(address),
receiver = VALUES(receiver),
phone = VALUES(phone),
remark = VALUES(remark),
create_time = VALUES(create_time),
update_time = VALUES(update_time);

INSERT INTO order_item (id, order_id, book_id, book_title, book_price, quantity, subtotal) VALUES
(1, 1, 2, 'Spring in Action, Sixth Edition', 126.00, 1, 126.00),
(96101, 95101, 1, 'Clean Code', 89.00, 1, 89.00),
(96102, 95101, 92009, 'JavaScript高级程序设计', 109.00, 1, 109.00),
(96103, 95102, 3, 'Java核心技术 卷I（原书第12版）', 149.00, 1, 149.00),
(96104, 95102, 92007, 'Spring实战', 98.00, 1, 98.00),
(96105, 95103, 2, 'Spring in Action, Sixth Edition', 126.00, 1, 126.00),
(96106, 95103, 92016, '纳瓦尔宝典', 64.00, 1, 64.00),
(96107, 95104, 92014, '算法图解', 79.00, 1, 79.00),
(96108, 95104, 92001, '时间简史', 58.00, 1, 58.00)
ON DUPLICATE KEY UPDATE
order_id = VALUES(order_id),
book_id = VALUES(book_id),
book_title = VALUES(book_title),
book_price = VALUES(book_price),
quantity = VALUES(quantity),
subtotal = VALUES(subtotal);

-- 公告补充
INSERT INTO notice (
    id, title, content, cover_image, status, priority, publish_time, create_time, update_time
) VALUES
(11, '春日书单已补齐真实封面', '图书广场与推荐页面已同步更多真实图书封面，热门与新书区域的阅读体验会更完整。', '/uploads/cover/seed-vue-design.jpg', 1, 9, '2026-04-02 09:45:00', '2026-04-02 09:45:00', '2026-04-02 09:45:00'),
(12, '技术书专区新增工程实践读物', '新增 Clean Code、Spring in Action、Java 核心技术等高频工程实践书目，便于前后端学习与检索。', '/uploads/cover/seed-clean-code.jpg', 1, 8, '2026-04-02 09:50:00', '2026-04-02 09:50:00', '2026-04-02 09:50:00'),
(13, '文学与科幻书架同步高质量封面', '活着、平凡的世界、三体、时间简史等图书已替换为真实封面展示，浏览体验更统一。', '/uploads/cover/seed-three-body.jpg', 1, 7, '2026-04-02 09:55:00', '2026-04-02 09:55:00', '2026-04-02 09:55:00')
ON DUPLICATE KEY UPDATE
title = VALUES(title),
content = VALUES(content),
cover_image = VALUES(cover_image),
status = VALUES(status),
priority = VALUES(priority),
publish_time = VALUES(publish_time),
create_time = VALUES(create_time),
update_time = VALUES(update_time);

-- 按评价数据回写评分，保证详情页与推荐页展示一致
UPDATE book b
SET b.rating = COALESCE((
    SELECT ROUND(AVG(r.rating), 1)
    FROM review r
    WHERE r.book_id = b.id
), b.rating)
WHERE b.id IN (1, 2, 3, 92001, 92002, 92004, 92005, 92007, 92008, 92009, 92011, 92012, 92014, 92016);

COMMIT;
