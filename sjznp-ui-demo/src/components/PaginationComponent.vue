<script setup>

import {ref} from "vue";

const pageSizeOptions = [20, 50, 100];

const {pageData} = defineProps({
  pageData: Object,
});

const pageSize = ref(pageSizeOptions[0]);
const pageNo = ref(0);

const emits = defineEmits(['goPage']);

const goPageAction = (pageNoParam, size) => {
  console.log('goPageAction entered');
  pageNo.value = pageNoParam;
  emits('goPage', pageNoParam, size ?? pageSize.value);
};

</script>

<template>
  <div class="part-data-page">
    <button type="button" @click="goPageAction(0)" :disabled="pageNo <= 0">首页</button>
    <button type="button" @click="goPageAction(pageNo - 1)" :disabled="pageNo <= 0">上一页</button>
    <span v-show="pageNo >= 2">...</span>
    <button type="button" @click="goPageAction(pageNo - 1)" v-show="pageNo > 0">{{ pageNo }}</button>
    <button type="button">{{ pageNo + 1 }}</button>
    <button type="button" @click="goPageAction(pageNo + 1)" v-show="pageNo < pageData.totalPages - 1">
      {{ pageNo + 2 }}
    </button>
    <span v-show="pageNo + 2 <= pageData.totalPages - 1">...</span>
    <button type="button" @click="goPageAction(pageNo + 1)" :disabled="pageNo >= pageData.totalPages - 1">下一页
    </button>
    <button type="button" @click="goPageAction(pageData.totalPages - 1)" :disabled="pageNo >= pageData.totalPages - 1">
      尾页
    </button>
    <span>{{ pageData.page + 1 }}/{{ pageData.totalPages }}页 ({{ pageData.size }}) (共{{
        pageData.total
      }}条)</span>

    <select v-model="pageSize"
            @change="goPageAction(Math.trunc(pageData.size *  pageNo / $event.target.value), $event.target.value)"
            class="page-select">
      <option v-for="i in pageSizeOptions" :value="i">{{ i }}</option>
    </select>
  </div>

</template>

<style scoped>
.part-data-page {
  margin-top: 12px;
  display: flex;
  gap: 4px;
  width: max-content;

  position: sticky;
  bottom: 0;
  backdrop-filter: blur(2px) brightness(80%);
}

.page-select {
  text-align: end;
}
</style>
