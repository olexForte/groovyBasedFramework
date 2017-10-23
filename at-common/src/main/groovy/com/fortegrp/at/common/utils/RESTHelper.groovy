package com.fortegrp.at.common.utils

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpResponseException
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import org.apache.http.impl.client.LaxRedirectStrategy

import static com.fortegrp.at.common.utils.LogHelper.logInfo

@Slf4j
final class RESTHelper {
    private static RESTClient client = null

    static synchronized RESTClient getClient(restUrl) {
        if (client == null) {
            client = new RESTClient(restUrl)
            client.client.setRedirectStrategy(new LaxRedirectStrategy())
        }
        return client
    }

    static doGet(path, expectedResponse = 200) {
        doGetWithAut(path, null, expectedResponse)
    }

    static doGetWithAut(path, apiKey, expectedResponse = 200, query = null, customHeaders = [:]) {
        logInfo(String.format("Sending HTTP GET request to: %s with apiKey: %s " + (query ? query : ""), path, apiKey))
        client.headers.clear()
        client.headers['Authorization'] = 'Bearer ' + apiKey
        if (customHeaders) {
            com.fortegrp.at.common.utils.LogHelper.logInfo("Custom headers " + customHeaders)
            customHeaders.keySet().each {
                client.headers[it] = customHeaders.get(it)
            }
        }
        def response
        try {
            response = (path.contains("://") ? client.get(uri: path, query: query) :
                    client.get(path: path, query: query))
        } catch (HttpResponseException e) {
            response = e.response
        }
        logInfo(String.format("HTTP response [%s]: %s", response.status, response.data))
        if (expectedResponse) {
            if (response.status != expectedResponse) {
                throw new RuntimeException("Error occurred during the sending HTTP GET request: " + response.status + ". " + response?.data)
            }
        }
        response
    }

    static doPostWithAut(args, apiKey, expectedResponse = 200) {
        logInfo(String.format("Sending HTTP POST request: %s with apiKey: %s ", args, apiKey))
        client.headers['Authorization'] = 'Bearer ' + apiKey
        def response
        try {
            response = client.post(args)
        } catch (HttpResponseException e) {
            response = e.response
        }
        logInfo(String.format("HTTP response [%s]: %s", response.status, response.data))
        if (expectedResponse) {
            if (response.status != expectedResponse) {
                throw new RuntimeException("Error occurred during the sending HTTP POST request: " + response.status + ". " + response?.data)
            }
        }
        response
    }

    static doSendMultipartFormWithAut(url, multipartForm, apiKey, expectedResponse = 200, Method sendType = Method.POST) {

        logInfo(String.format("Sending Multipart HTTP POST request with apiKey: %s ", apiKey))
        client.headers['Authorization'] = 'Bearer ' + apiKey
        def response
        try {
            response = client.request(sendType) { req ->
                uri.path = url
                req.entity = multipartForm
            }
        } catch (HttpResponseException e) {
            response = e.response
        }
        logInfo(String.format("HTTP response [%s]: %s", response.status, response?.data))
        if (expectedResponse) {
            if (response.status != expectedResponse) {
                throw new AssertionError("Error occurred during the sending HTTP MULTIPART POST request: " + response.status + ". " + response?.data)
            }

        }

        response
    }

    static doPostMultipartFormWithAut(url, multipartForm, apiKey, expectedResponse = 200) {
        doSendMultipartFormWithAut(url, multipartForm, apiKey, expectedResponse, Method.POST)
    }

    static doPutMultipartFormWithAut(url, multipartForm, apiKey, expectedResponse = 200) {
        doSendMultipartFormWithAut(url, multipartForm, apiKey, expectedResponse, Method.PUT)
    }

    static doPutWithAut(args, apiKey, expectedResponse = 200) {
        logInfo(String.format("Sending HTTP PUT request: %s with apiKey: %s ", args, apiKey))
        client.headers['Authorization'] = 'Bearer ' + apiKey
        def response
        try {
            response = client.put(args)
        } catch (HttpResponseException e) {
            response = e.response
        }
        logInfo(String.format("HTTP response [%s]: %s", response.status, response.data))
        if (expectedResponse) {
            if (response.status != expectedResponse) {
                throw new RuntimeException("Error occurred during the sending HTTP PUT request: " + response.status + ". " + response?.data)
            }
        }
        response
    }

    static doDeleteWithAut(args, apiKey, expectedResponse = 204) {
        logInfo(String.format("Sending HTTP DELETE request: %s with apiKey: %s ", args, apiKey))
        client.headers['Authorization'] = 'Bearer ' + apiKey

        def response
        try {
            response = client.delete(args)
        } catch (HttpResponseException e) {
            response = e.response
        }
        logInfo(String.format("HTTP response [%s]: %s", response.status, response.data))
        if (expectedResponse) {
            if (response.status != expectedResponse) {
                throw new RuntimeException("Error occurred during the sending HTTP DELETE request: " + response.status + ". " + response?.data)
            }
        }
        response
    }


    static doPut(args, expectedResponse = 200) {
        doPutWithAut(args, null, expectedResponse)
    }

    static doPost(args, expectedResponse = 200) {
        doPostWithAut(args, null, expectedResponse)
    }

    static doPostMultipartForm(url, multipartForm, expectedResponse = 200) {
        doPostMultipartFormWithAut(url, multipartForm, null, expectedResponse)
    }

}

