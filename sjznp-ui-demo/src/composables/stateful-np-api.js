import {queryPrices as rawQueryPrices} from "@/api/np-api.js";
import {ref, toValue} from "vue";

export const useStatefulPricesRequest = () => {
    let controller = new AbortController();
    const running = ref(false);
    const error = ref(false);
    const data = ref(null);

    const abort = () => controller?.abort();

    const queryPrices = (params) => {
        controller?.abort();
        controller = new AbortController();

        running.value = true;
        error.value = false;

        return rawQueryPrices(toValue(params), controller.signal)
            .finally(() => running.value = false)
            .then(resp => {
                error.value = false;
                data.value = resp.data;
                return resp.data;
            })
            .catch(err => {
                console.log('useStatefulPricesRequest, catch entered', err);
                error.value = true;
                return Promise.reject(err);
            });
    };

    return {queryPrices, data, abort, running, error};

};
