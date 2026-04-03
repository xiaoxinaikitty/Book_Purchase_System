<template>
  <div class="recommend-page">
    <section class="recommend-hero">
      <div class="hero-copy">
        <el-button text class="back-btn" @click="goBack">← 返回</el-button>
        <div class="hero-kicker">Smart Picks</div>
        <h1>给你一份更懂你的温柔书单</h1>
        <p>
          把个性推荐、猜你喜欢、热读趋势和新书节奏整理成一页更舒服的浏览体验，让推荐结果不只准确，也更有温度。
        </p>

        <div class="hero-actions">
          <el-button type="primary" round @click="router.push('/books')">去图书广场</el-button>
          <el-button round @click="loadAll">刷新推荐</el-button>
        </div>

        <div class="hero-chips">
          <span class="hero-chip">专属推荐 {{ personalList.length }}</span>
          <span class="hero-chip">猜你喜欢 {{ guessList.length }}</span>
          <span class="hero-chip">热门趋势 {{ hotList.length }}</span>
          <span class="hero-chip">新书速递 {{ newList.length }}</span>
        </div>
      </div>

      <div class="hero-panel">
        <div class="panel-kicker">推荐速览</div>
        <h3>本轮推荐已经为你铺好书架</h3>
        <div class="signal-grid">
          <div class="signal-card">
            <strong>{{ personalList.length }}</strong>
            <span>个性化精选</span>
          </div>
          <div class="signal-card">
            <strong>{{ guessList.length }}</strong>
            <span>兴趣延展</span>
          </div>
          <div class="signal-card">
            <strong>{{ hotList.length }}</strong>
            <span>热度趋势</span>
          </div>
          <div class="signal-card">
            <strong>{{ newList.length }}</strong>
            <span>新鲜发现</span>
          </div>
        </div>

        <div class="mood-tags">
          <span v-for="tag in recommendationTags" :key="tag" class="mood-tag">{{ tag }}</span>
        </div>
      </div>
    </section>

    <section class="recommend-board">
      <article class="feature-panel" v-loading="loading.personal">
        <div class="panel-header">
          <div>
            <span class="panel-kicker warm">专属推荐</span>
            <h2>为你推荐</h2>
            <p>基于相似用户评分与阅读偏好，为你优先放大最值得先看的那几本。</p>
          </div>
        </div>

        <div v-if="personalLead" class="feature-layout">
          <div class="feature-spotlight" @click="goDetail(personalLead.id)">
            <img :src="personalLead.coverImage || defaultCover" alt="cover" />
            <div class="feature-overlay">
              <span class="spotlight-pill">优先阅读</span>
              <h3>{{ personalLead.title }}</h3>
              <p>{{ personalLead.author || '未知作者' }}</p>
              <div class="spotlight-meta">
                <span>评分 {{ personalLead.rating || 0 }}</span>
                <span>¥{{ formatPrice(personalLead.price) }}</span>
              </div>
            </div>
          </div>

          <div class="compact-list">
            <div
              v-for="book in personalTrail"
              :key="book.id"
              class="compact-card"
              @click="goDetail(book.id)"
            >
              <img :src="book.coverImage || defaultCover" alt="cover" />
              <div class="compact-copy">
                <strong>{{ book.title }}</strong>
                <span>{{ book.author || '未知作者' }}</span>
                <em>¥{{ formatPrice(book.price) }}</em>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂时没有生成个性化推荐" />
      </article>

      <aside class="insight-column">
        <div class="insight-card">
          <span class="panel-kicker cool">推荐依据</span>
          <h3>这一页如何帮你挑书</h3>
          <ul class="insight-list">
            <li>先看专属推荐，优先处理和你兴趣最接近的书。</li>
            <li>再看猜你喜欢，适合从熟悉主题延展到相近方向。</li>
            <li>热门榜与新书榜则负责补充时下趋势与最新上架。</li>
          </ul>
        </div>

        <div class="insight-card soft">
          <span class="panel-kicker soft-tag">浏览建议</span>
          <h3>推荐页最佳打开方式</h3>
          <div class="tip-stack">
            <div class="tip-item">
              <strong>先看大卡片</strong>
              <span>优先聚焦本轮最值得点击的一本书。</span>
            </div>
            <div class="tip-item">
              <strong>再扫趋势榜</strong>
              <span>快速判断现在大家在看什么、有什么新内容。</span>
            </div>
          </div>
        </div>
      </aside>
    </section>

    <section class="recommend-lower">
      <article class="guess-panel" v-loading="loading.guess">
        <div class="panel-header">
          <div>
            <span class="panel-kicker gold">猜你喜欢</span>
            <h2>兴趣延展书单</h2>
            <p>综合行为与热度的混合推荐，适合继续往相近阅读方向探索。</p>
          </div>
        </div>

        <div v-if="guessList.length" class="guess-grid">
          <div v-for="book in guessList" :key="book.id" class="guess-card" @click="goDetail(book.id)">
            <div class="guess-cover">
              <img :src="book.coverImage || defaultCover" alt="cover" />
              <span class="guess-tag">{{ book.categoryName || '为你扩展' }}</span>
            </div>
            <div class="guess-body">
              <div class="guess-topline">
                <span>评分 {{ book.rating || 0 }}</span>
                <strong>¥{{ formatPrice(book.price) }}</strong>
              </div>
              <h3>{{ book.title }}</h3>
              <p>{{ book.author || '未知作者' }}</p>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂时没有猜你喜欢数据" />
      </article>

      <div class="ranking-column">
        <article class="ranking-panel" v-loading="loading.hot">
          <div class="panel-header">
            <div>
              <span class="panel-kicker warm">热门榜单</span>
              <h2>大家都在读</h2>
              <p>销量领先，适合快速建立当前热门感知。</p>
            </div>
          </div>

          <div v-if="hotList.length" class="ranking-list">
            <div
              v-for="(book, index) in hotList"
              :key="book.id"
              class="ranking-item"
              @click="goDetail(book.id)"
            >
              <span class="ranking-index">{{ index + 1 }}</span>
              <img :src="book.coverImage || defaultCover" alt="cover" />
              <div class="ranking-copy">
                <strong>{{ book.title }}</strong>
                <span>{{ book.author || '未知作者' }}</span>
              </div>
              <em>¥{{ formatPrice(book.price) }}</em>
            </div>
          </div>

          <el-empty v-else description="暂无热门榜单" />
        </article>

        <article class="ranking-panel light" v-loading="loading.new">
          <div class="panel-header">
            <div>
              <span class="panel-kicker cool">新书速递</span>
              <h2>刚刚上架</h2>
              <p>追踪最近更新内容，让你的书单持续有新鲜感。</p>
            </div>
          </div>

          <div v-if="newList.length" class="ranking-list">
            <div
              v-for="(book, index) in newList"
              :key="book.id"
              class="ranking-item light-item"
              @click="goDetail(book.id)"
            >
              <span class="ranking-index">{{ index + 1 }}</span>
              <img :src="book.coverImage || defaultCover" alt="cover" />
              <div class="ranking-copy">
                <strong>{{ book.title }}</strong>
                <span>{{ book.categoryName || book.author || '最新上架' }}</span>
              </div>
              <em>¥{{ formatPrice(book.price) }}</em>
            </div>
          </div>

          <el-empty v-else description="暂无新书速递" />
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  getPersonalRecommend,
  getGuessRecommend,
  getHotRecommend,
  getNewRecommend
} from '@/api/recommend'

const router = useRouter()
const defaultCover = '/vite.svg'

const personalList = ref([])
const guessList = ref([])
const hotList = ref([])
const newList = ref([])

const loading = reactive({
  personal: false,
  guess: false,
  hot: false,
  new: false
})

const personalLead = computed(() => personalList.value[0] || null)
const personalTrail = computed(() => personalList.value.slice(1, 5))

const recommendationTags = computed(() => {
  const seeds = [
    personalLead.value?.categoryName,
    personalLead.value?.author,
    guessList.value[0]?.categoryName,
    hotList.value[0]?.author,
    '温柔选书'
  ]

  return [...new Set(seeds.filter(Boolean))].slice(0, 4)
})

const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

const goDetail = (id) => {
  router.push(`/book/${id}`)
}

const loadPersonal = async () => {
  loading.personal = true
  try {
    const res = await getPersonalRecommend({ limit: 8 })
    personalList.value = res.data || []
  } catch (error) {
    console.error('获取个性化推荐失败:', error)
  } finally {
    loading.personal = false
  }
}

const loadGuess = async () => {
  loading.guess = true
  try {
    const res = await getGuessRecommend({ limit: 8 })
    guessList.value = res.data || []
  } catch (error) {
    console.error('获取猜你喜欢失败:', error)
  } finally {
    loading.guess = false
  }
}

const loadHot = async () => {
  loading.hot = true
  try {
    const res = await getHotRecommend({ limit: 6 })
    hotList.value = res.data || []
  } catch (error) {
    console.error('获取热门推荐失败:', error)
  } finally {
    loading.hot = false
  }
}

const loadNew = async () => {
  loading.new = true
  try {
    const res = await getNewRecommend({ limit: 6 })
    newList.value = res.data || []
  } catch (error) {
    console.error('获取新书推荐失败:', error)
  } finally {
    loading.new = false
  }
}

const loadAll = () => {
  loadPersonal()
  loadGuess()
  loadHot()
  loadNew()
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/home')
  }
}

onMounted(() => {
  loadAll()
})
</script>

<style scoped>
.recommend-page {
  --rose: #f7d9c7;
  --cream: #fffaf5;
  --amber: #dd8b4a;
  --coffee: #6b4f42;
  --sage: #759783;
  display: grid;
  gap: 22px;
  padding-bottom: 28px;
}

.recommend-hero {
  position: relative;
  overflow: hidden;
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(320px, 0.86fr);
  gap: 18px;
  padding: 32px;
  border-radius: 30px;
  background:
    radial-gradient(circle at 100% 0%, rgba(255, 255, 255, 0.45), transparent 28%),
    linear-gradient(135deg, #fff1e0 0%, #f7dfcf 48%, #e9dff8 100%);
  box-shadow: 0 24px 60px rgba(140, 106, 82, 0.14);
}

.recommend-hero::before,
.recommend-hero::after {
  content: '';
  position: absolute;
  border-radius: 50%;
}

.recommend-hero::before {
  width: 220px;
  height: 220px;
  right: -50px;
  top: -80px;
  background: rgba(255, 255, 255, 0.42);
}

.recommend-hero::after {
  width: 180px;
  height: 180px;
  right: 220px;
  bottom: -70px;
  background: rgba(255, 216, 187, 0.5);
}

.hero-copy,
.hero-panel {
  position: relative;
  z-index: 1;
}

.back-btn {
  padding-left: 0;
  color: #8f5f45;
  margin-bottom: 10px;
}

.back-btn:hover {
  color: #6e4834;
}

.hero-kicker,
.panel-kicker {
  display: inline-flex;
  padding: 7px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.hero-kicker {
  background: rgba(255, 255, 255, 0.7);
  color: #ad6640;
  text-transform: uppercase;
}

.hero-copy h1 {
  margin: 16px 0 12px;
  font-size: 40px;
  line-height: 1.12;
  color: #5d3f34;
}

.hero-copy p {
  max-width: 720px;
  line-height: 1.8;
  color: rgba(93, 63, 52, 0.82);
}

.hero-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.hero-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}

.hero-chip {
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  color: #8d6248;
  font-size: 12px;
}

.hero-panel {
  padding: 24px;
  border-radius: 28px;
  background: rgba(255, 253, 249, 0.82);
  border: 1px solid rgba(236, 218, 204, 0.9);
  box-shadow: 0 18px 34px rgba(156, 120, 96, 0.12);
}

.panel-kicker {
  background: rgba(117, 151, 131, 0.14);
  color: #648673;
}

.panel-kicker.warm {
  background: rgba(221, 139, 74, 0.14);
  color: #d17b39;
}

.panel-kicker.gold {
  background: rgba(238, 197, 126, 0.22);
  color: #bc8a2f;
}

.panel-kicker.cool {
  background: rgba(117, 151, 131, 0.14);
  color: #648673;
}

.panel-kicker.soft-tag {
  background: rgba(207, 180, 231, 0.2);
  color: #8a66b2;
}

.hero-panel h3,
.insight-card h3 {
  margin-top: 16px;
  font-size: 24px;
  color: var(--coffee);
}

.signal-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 20px;
}

.signal-card {
  padding: 16px;
  border-radius: 20px;
  background: #fff;
}

.signal-card strong {
  display: block;
  font-size: 24px;
  color: var(--coffee);
}

.signal-card span {
  display: block;
  margin-top: 6px;
  color: #927668;
  font-size: 12px;
}

.mood-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.mood-tag {
  padding: 8px 12px;
  border-radius: 999px;
  background: #fff4ea;
  color: #936648;
  font-size: 12px;
}

.recommend-board {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) 320px;
  gap: 18px;
}

.feature-panel,
.guess-panel,
.ranking-panel,
.insight-card {
  border-radius: 28px;
  background: rgba(255, 253, 249, 0.96);
  border: 1px solid rgba(236, 218, 204, 0.9);
  box-shadow: 0 18px 42px rgba(126, 94, 66, 0.08);
}

.feature-panel,
.guess-panel,
.ranking-panel,
.insight-card {
  padding: 24px;
}

.insight-column {
  display: grid;
  gap: 16px;
}

.insight-card.soft {
  background: linear-gradient(180deg, #fffaf7, #fef4ff);
}

.insight-list {
  margin: 18px 0 0;
  padding-left: 18px;
  color: #8c7264;
  line-height: 1.8;
}

.tip-stack {
  display: grid;
  gap: 12px;
  margin-top: 18px;
}

.tip-item {
  padding: 16px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.8);
}

.tip-item strong {
  display: block;
  color: var(--coffee);
}

.tip-item span {
  display: block;
  margin-top: 6px;
  color: #927668;
  line-height: 1.7;
}

.panel-header h2 {
  margin: 14px 0 8px;
  font-size: 28px;
  color: var(--coffee);
}

.panel-header p {
  color: #907466;
  line-height: 1.7;
}

.feature-layout {
  display: grid;
  grid-template-columns: minmax(280px, 420px) minmax(240px, 1fr);
  gap: 16px;
  margin-top: 20px;
}

.feature-spotlight {
  position: relative;
  min-height: 300px;
  max-height: 320px;
  border-radius: 24px;
  overflow: hidden;
  cursor: pointer;
  background: #ebd7c8;
}

.feature-spotlight img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #f5f0eb;
}

.feature-overlay {
  position: absolute;
  inset: auto 0 0;
  padding: 20px;
  background: linear-gradient(180deg, transparent, rgba(41, 28, 21, 0.84));
  color: #fffaf4;
}

.spotlight-pill {
  display: inline-flex;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.2);
  font-size: 12px;
  font-weight: 700;
}

.feature-overlay h3 {
  margin-top: 12px;
  font-size: 28px;
}

.feature-overlay p {
  margin-top: 8px;
  color: rgba(255, 250, 244, 0.82);
}

.spotlight-meta {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-top: 14px;
  font-size: 13px;
}

.compact-list {
  display: grid;
  gap: 12px;
}

.compact-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 18px;
  background: #fff;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.compact-card:hover,
.ranking-item:hover,
.guess-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 14px 28px rgba(150, 108, 77, 0.1);
}

.compact-card img {
  width: 72px;
  height: 98px;
  border-radius: 14px;
  object-fit: contain;
  flex-shrink: 0;
  background: #f5f0eb;
}

.compact-copy {
  display: grid;
  align-content: center;
  gap: 6px;
}

.compact-copy strong,
.guess-body h3,
.ranking-copy strong {
  color: var(--coffee);
}

.compact-copy span,
.guess-body p,
.ranking-copy span {
  color: #8f7263;
  font-size: 12px;
}

.compact-copy em {
  font-style: normal;
  color: var(--amber);
  font-weight: 700;
}

.recommend-lower {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) 380px;
  gap: 18px;
}

.guess-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-top: 20px;
}

.guess-card {
  overflow: hidden;
  border-radius: 22px;
  background: #fff;
  border: 1px solid rgba(239, 225, 212, 0.92);
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.guess-cover {
  position: relative;
  padding: 14px 14px 0;
}

.guess-cover img {
  width: 100%;
  height: 220px;
  border-radius: 18px;
  object-fit: contain;
  background: #efe0d5;
}

.guess-tag {
  position: absolute;
  left: 26px;
  bottom: 14px;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 250, 244, 0.92);
  color: #936648;
  font-size: 12px;
}

.guess-body {
  padding: 16px;
}

.guess-topline {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  color: #7c8870;
  font-size: 12px;
}

.guess-topline strong {
  color: var(--amber);
  font-size: 20px;
}

.guess-body h3 {
  margin-top: 14px;
  font-size: 18px;
  line-height: 1.45;
}

.guess-body p {
  margin-top: 8px;
}

.ranking-column {
  display: grid;
  gap: 16px;
  align-content: start;
}

.ranking-panel.light {
  background: linear-gradient(180deg, #fffdf9, #f7fcf8);
}

.ranking-list {
  display: grid;
  gap: 12px;
  margin-top: 20px;
}

.ranking-item {
  display: grid;
  grid-template-columns: 36px 72px minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  padding: 12px;
  border-radius: 18px;
  background: #fff;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.ranking-item.light-item {
  background: #fcfffd;
}

.ranking-index {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #efb37d, #db7b46);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
}

.ranking-item img {
  width: 72px;
  height: 96px;
  border-radius: 14px;
  object-fit: contain;
  background: #efe0d5;
}

.ranking-copy {
  display: grid;
  gap: 6px;
}

.ranking-item em {
  font-style: normal;
  color: var(--amber);
  font-weight: 700;
}

@media (max-width: 1280px) {
  .recommend-hero,
  .recommend-board,
  .recommend-lower {
    grid-template-columns: minmax(0, 1fr);
  }
}

@media (max-width: 980px) {
  .feature-layout,
  .guess-grid,
  .signal-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}

@media (max-width: 720px) {
  .recommend-hero,
  .feature-panel,
  .guess-panel,
  .ranking-panel,
  .insight-card {
    padding: 18px;
  }

  .hero-copy h1 {
    font-size: 30px;
  }

  .hero-actions {
    flex-direction: column;
  }

  .ranking-item {
    grid-template-columns: 32px 64px minmax(0, 1fr);
  }

  .ranking-item em {
    grid-column: 2 / 4;
    justify-self: end;
  }
}

@media (max-width: 560px) {
  .hero-chips,
  .mood-tags {
    display: grid;
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>


