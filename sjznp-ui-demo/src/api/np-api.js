import axios from "axios";

// http://localhost:8080/api
export const queryPrices = (params, signal) => axios.get('/api/', {
    params: params,
    signal: signal
});
