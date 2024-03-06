/******************************************************************************
 *                               RAIM API CALLS                               *
 ******************************************************************************/
class SubscriptionApi {

    /**
     * The Raim API Class Constructor.
     */
    constructor() {

    }

    /**
     * API Generate Subscription function.
     *
     * @param  {string} mrn                     The service MRN
     * @param  {any} subscriptionRequestObject  The subscription request object
     * @param  {Function} callback              The callback to be used after the AJAX call
     * @param  {Function} errorCallback         The error callback to be used if the AJAX call fails
     */
    subscribe(mrn, subscriptionRequestObject, callback, errorCallback) {
        $.ajax({
            url: `./api/subscription/${mrn}`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(subscriptionRequestObject),
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }

    /**
     * API Remove Subscription function.
     *
     * @param  {string} mrn                     The service MRN
     * @param  {Function} callback              The callback to be used after the AJAX call
     * @param  {Function} errorCallback         The error callback to be used if the AJAX call fails
     */
    unsubscribe(mrn, callback, errorCallback) {
        $.ajax({
            url: `./api/subscription/${mrn}`,
            type: 'DELETE',
            contentType: 'application/json',
            success: callback,
            error: (response, status, more) => handleAjaxError(response, status, more, errorCallback)
        });
    }
}
/******************************************************************************/