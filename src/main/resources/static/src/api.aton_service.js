/******************************************************************************
 *                          ATON SERVICE API CALLS                            *
 ******************************************************************************/
class AtonServiceApi {

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
    getAtonServices(callback, errorCallback) {
        $.ajax({
            url: `./api/aton_service`,
            type: 'GET',
            contentType: 'application/json',
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }

    /**
     * API Get S-125 AtoN service datasets function.
     *
     * @param  {string} mrn                     The AtoN service MRN
     * @param  {Function} callback              The callback to be used after the AJAX call
     * @param  {Function} errorCallback         The error callback to be used if the AJAX call fails
     */
    getAtonDatasets(mrn, callback, errorCallback) {
        $.ajax({
            url: `./api/aton_service/${mrn}/summary`,
            type: 'GET',
            contentType: 'application/json',
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }

    /**
     * API Get S-125 AtoN service dataset content function.
     *
     * @param  {string} mrn                     The AtoN service MRN
     * @param  {Function} callback              The callback to be used after the AJAX call
     * @param  {Function} errorCallback         The error callback to be used if the AJAX call fails
     */
    getAtonDatasetContent(mrn, uuid, callback, errorCallback) {
        $.ajax({
            url: `./api/aton_service/${mrn}/content?dataReference=${uuid}`,
            type: 'GET',
            contentType: 'application/json',
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }
}
/******************************************************************************/