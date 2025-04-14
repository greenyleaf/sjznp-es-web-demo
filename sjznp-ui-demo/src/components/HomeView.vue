<script setup>

import {ref} from "vue";
import {useStatefulPricesRequest} from "@/composables/stateful-np-api.js";
import PaginationComponent from "@/components/PaginationComponent.vue";
import PriceTable from "@/components/PriceTable.vue";
import SearchFilter from "@/components/SearchFilter.vue";

const pageSize = ref(20);
const filter = ref({});

const {queryPrices, data: pageData, running, error} = useStatefulPricesRequest();

/*const doSearch = async () => {
  // const res = await queryPrices({...filter.value, page: page.value, size: pageSize.value});
  // pageData.value = res.data;

  await doQuest({...filter.value, page: page.value, size: pageSize.value});
}*/

const searchHandler = (filterParam) => {
  filter.value = filterParam;
  queryPrices({...filter.value, page: 0, size: pageSize.value});
};

const goPageHandler = (pageNo, size) => {
  console.log('goPageHandler entered');
  console.log(pageNo, size);
  pageSize.value = size;
  queryPrices({...filter.value, page: pageNo, size: size});
};

searchHandler();

</script>

<template>
  <main>
    <div>价格行情</div>

    <search-filter @search="searchHandler"></search-filter>

    <div v-if="pageData" class="part-data">
      <PriceTable :page-data="pageData" :running="running"></PriceTable>

      <pagination-component :page-data="pageData" v-model:page-size="pageSize"
                            @go-page="goPageHandler"></pagination-component>
    </div>
    <div v-else>点击查询</div>

  </main>
</template>

<style scoped>
.part-data {
  margin-top: 12px;
  position: relative;
}
</style>
