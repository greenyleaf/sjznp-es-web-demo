<script setup>
import typeNames from "@/assets/type-names.json";
import {ref} from "vue";

const initialFilter = Object.freeze({typeName: '', exact: false});

const emits = defineEmits(['search']);

const filter = ref({...initialFilter});

const searchAction = () => {
  emits('search', filter.value);
};

const resetAction = () => {
  filter.value = {...initialFilter};
};

</script>

<template>
  <form class="filter-box" action="javascript:" @submit="searchAction">
    <select v-model="filter.typeName" class="filter-select">
      <option value="">选择种类</option>
      <option v-for="i in typeNames" :value="i" v-text="i"></option>
    </select>
    <input type="date" v-model="filter.date" placeholder="筛选日期">
    <input placeholder="商品名称" v-model="filter.name" accept="N">
    <input type="checkbox" v-model="filter.exact" placeholder="精确匹配" title="精确匹配">

    <button type="submit" class="filter-box-btn" accesskey="Q">查询</button>

    <button type="reset" @click="resetAction" class="filter-box-btn filter-box-btn-reset" accesskey="C">清空</button>
  </form>
</template>

<style scoped>
.filter-box {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.filter-select {
  width: 90px;
  text-align: center;
}

.filter-box-btn {
  --main-color: darkorange;
  --sec-color: orangered;

  padding: 4px 8px;
  border: none;
  border-radius: 6px;
  box-shadow: inset 0 0 0 1px gray, inset 0 0 4px 2px white;
  background-color: var(--main-color);
  outline: none;
}

.filter-box-btn:focus {
  outline: var(--sec-color) solid 1px;
}

.filter-box-btn-reset {
  --main-color: dodgerblue;
  --sec-color: lightskyblue;
}
</style>
