<script setup>

defineProps({'pageData': Object, 'running': Boolean});

const twoDigits = (price) => {
  return price.toFixed(2);
};

</script>

<template>
  <table v-if="pageData?.items?.length" class="part-data-table" :class="{'part-data-table-running': running}"
         style="position: relative;">
    <thead class="part-data-header">
    <tr class="part-data-header-row">
      <th class="part-data-header-cell">种类</th>
      <th class="part-data-header-cell">品名</th>
      <th class="part-data-header-cell">最低价(千克)</th>
      <th class="part-data-header-cell">平均价(千克)</th>
      <th class="part-data-header-cell">最高价(千克)</th>
      <th class="part-data-header-cell">发布日期</th>
    </tr>
    </thead>

    <tbody>
    <tr v-for="i in pageData?.items" class="part-data-item">
      <td class="part-data-cell">{{ i.typeName }}</td>
      <td>{{ i.name }}</td>
      <td>￥{{ twoDigits(i.min) }}</td>
      <td>￥{{ twoDigits(i.avg) }}</td>
      <td>￥{{ twoDigits(i.max) }}</td>
      <td>{{ i.date }}</td>
    </tr>
    </tbody>
  </table>

  <div v-else>没有数据</div>
</template>

<style scoped>
.part-data-header {
  position: sticky;
  top: 0;
  z-index: 100;
}

.part-data-header-row {
  font-weight: bold;

  background-color: lightseagreen;
}

.part-data-header-cell, .part-data-cell {
  padding: 6px 0;
  font-size: 17px;
}

.part-data-header-row, .part-data-item {
  display: grid;
  grid-template-columns: 80px 180px repeat(3, 136px) 136px;
  text-align: center;
  justify-content: start;
  align-items: center;
}

.part-data-item:nth-child(odd) {
  background-color: lightblue;
}

@keyframes row-hover-anim {
  from, 5%, 95%, to {
    background-color: #90CAF9;
  }
  45%, 55% {
    background-color: #64B5F6;
  }
}

.part-data-item:hover {
  /*background-color: lightsalmon;*/
  animation: 1.6s infinite row-hover-anim ease-in-out;
}

.part-data-table {
  transition: filter .8s;
}

.part-data-table-running {
  filter: blur(1px) brightness(118%) contrast(88%);
}
</style>
