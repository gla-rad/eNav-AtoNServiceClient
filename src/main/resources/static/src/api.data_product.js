/******************************************************************************
 *                          DATA PRODUCT API CALLS                            *
 ******************************************************************************/
class DataProductApi {

    /**
     * The Raim API Class Constructor.
     */
    constructor() {

    }

    /**
     * API Get Registered S-125 AtoN Services function.
     *
     * @param  {string} mrn                     The service MRN
     * @param  {Function} callback              The callback to be used after the AJAX call
     * @param  {Function} errorCallback         The error callback to be used if the AJAX call fails
     */
    getServices(dataProductId, callback, errorCallback) {
        $.ajax({
            url: `./api/data_product/${dataProductId}/services`,
            type: 'GET',
            contentType: 'application/json',
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }

}
/******************************************************************************/