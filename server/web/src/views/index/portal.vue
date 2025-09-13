<template>
  <div class="portal">
    <Header />
    <Content />
    <Footer />
    <AiChat />
  </div>
</template>
<script>
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import Content from '/@/views/index/components/content.vue'
import AiChat from '/@/views/index/components/ai-chat.vue'

export default {
  components: {
    Footer,
    Header,
    Content,
    AiChat
  },
  data () {
    return {
    }
  }
}
</script>
<style scoped lang="less">

</style>