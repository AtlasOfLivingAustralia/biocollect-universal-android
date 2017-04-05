package au.org.ala.mobile.ozatlas

enum class ApiEndpoints constructor(val endpointName: String, val url: String?) {
    PRODUCTION("Production", ApiModule.PRODUCTION_BIOCOLLECT_URL.toString()),
    TEST("Test", ApiModule.TEST_BIOCOLLECT_URL),
    MOCK_MODE("Mock Mode", "http://localhost/mock/"),
    CUSTOM("Custom", null);

    override fun toString() = endpointName

    companion object {

        fun from(endpoint: String): ApiEndpoints {
            for (value in values()) {
                if (value.url != null && value.url == endpoint) {
                    return value
                }
            }
            return CUSTOM
        }

        fun isMockMode(endpoint: String): Boolean {
            return from(endpoint) == MOCK_MODE
        }
    }
}