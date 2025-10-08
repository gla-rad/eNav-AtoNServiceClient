/******************************************************************************
 *                          SECOM SERVICE API CALLS                           *
 ******************************************************************************/
class SecomServiceApi {

    /**
     * The Raim API Class Constructor.
     */
    constructor() {

    }

    /**
     * API Get Secom services function, currently registered in the MPC.
     *
     * @param  {string} dataProductType         The SECOM Data Product ID
     * @param  {Function} callback              The callback to be used after the AJAX call
     * @param  {Function} errorCallback         The error callback to be used if the AJAX call fails
     */
    getSecomServices(dataProductType, callback, errorCallback) {
        $.ajax({
            url: `./api/secom_service?` + (dataProductType?`dataProductType=${dataProductType}`:``),
            type: 'GET',
            contentType: 'application/json',
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }

    /**
     * API Get SECOM service datasets function.
     *
     * @param  {string} mrn                     The SECOM service MRN
     * @param  {string} dataProductType         The SECOM service data product type
     * @param  {Function} callback              The callback to be used after the AJAX call
     * @param  {Function} errorCallback         The error callback to be used if the AJAX call fails
     */
    getSecomDatasets(mrn, dataProductType, callback, errorCallback) {
        var encodedDataProductType = encodeURI(dataProductType);
        $.ajax({
            url: `./api/secom_service/${mrn}/summary?` + (dataProductType?`dataProductType=${encodedDataProductType}`:``),
            type: 'GET',
            contentType: 'application/json',
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }

    /**
     * API Get SECOM service dataset content function.
     *
     * @param  {string} mrn                     The SECOM service MRN
     * @param  {string} dataProductType         The SECOM service data product type
     * @param  {string} uuid                    The SECOM service dataset UUID
     * @param  {Function} callback              The callback to be used after the AJAX call
     * @param  {Function} errorCallback         The error callback to be used if the AJAX call fails
     */
    getSecomDatasetContent(mrn, dataProductType, uuid, geometry, callback, errorCallback) {
        var encodedDataProductType = encodeURI(dataProductType);
        var encodedUuid = encodeURI(uuid);
        var encodedGeometry = encodeURI(geometry);
        $.ajax({
            url: `./api/secom_service/${mrn}/content?` + (dataProductType?`dataProductType=${encodedDataProductType}&`:``) + (uuid?`dataReference=${encodedUuid}`:``) + (geometry?`geometry=${encodedGeometry}`:``),
            type: 'GET',
            contentType: 'application/json',
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }
}
/******************************************************************************/