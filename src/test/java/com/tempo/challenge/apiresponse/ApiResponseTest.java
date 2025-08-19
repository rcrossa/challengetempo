package com.tempo.challenge.apiresponse;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void constructor_shouldInitializeAllFields() {
        String message = "Test message";
        String data = "Test data";

        ApiResponse<String> response = new ApiResponse<>(true, message, data);

        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void constructor_withNullData_shouldWork() {
        String message = "Test message";

        ApiResponse<String> response = new ApiResponse<>(false, message, null);

        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void setSuccess_shouldUpdateSuccessFlag() {
        ApiResponse<String> response = new ApiResponse<>(true, "message", "data");

        response.setSuccess(false);

        assertFalse(response.isSuccess());
    }

    @Test
    void setMessage_shouldUpdateMessage() {
        ApiResponse<String> response = new ApiResponse<>(true, "old message", "data");
        String newMessage = "new message";

        response.setMessage(newMessage);

        assertEquals(newMessage, response.getMessage());
    }

    @Test
    void setData_shouldUpdateData() {
        ApiResponse<String> response = new ApiResponse<>(true, "message", "old data");
        String newData = "new data";

        response.setData(newData);

        assertEquals(newData, response.getData());
    }

    @Test
    void setData_withNull_shouldWork() {
        ApiResponse<String> response = new ApiResponse<>(true, "message", "data");

        response.setData(null);

        assertNull(response.getData());
    }

    @Test
    void apiResponse_withDifferentDataTypes_shouldWork() {
        // Test with Integer
        ApiResponse<Integer> intResponse = new ApiResponse<>(true, "Number", 42);
        assertEquals(Integer.valueOf(42), intResponse.getData());

        // Test with Boolean
        ApiResponse<Boolean> boolResponse = new ApiResponse<>(false, "Boolean", true);
        assertEquals(Boolean.TRUE, boolResponse.getData());

        // Test with custom object
        TestObject testObj = new TestObject("test");
        ApiResponse<TestObject> objResponse = new ApiResponse<>(true, "Object", testObj);
        assertEquals(testObj, objResponse.getData());
    }

    @Test
    void apiResponse_successfulResponse_scenario() {
        String data = "Operation completed";
        ApiResponse<String> response = new ApiResponse<>(true, "Success", data);

        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void apiResponse_errorResponse_scenario() {
        ApiResponse<Void> response = new ApiResponse<>(false, "Error occurred", null);

        assertFalse(response.isSuccess());
        assertEquals("Error occurred", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void apiResponse_modifyFields_afterCreation() {
        ApiResponse<String> response = new ApiResponse<>(false, "initial", "initial data");

        // Modify all fields
        response.setSuccess(true);
        response.setMessage("updated message");
        response.setData("updated data");

        assertTrue(response.isSuccess());
        assertEquals("updated message", response.getMessage());
        assertEquals("updated data", response.getData());
    }

    // Helper class for testing
    private static class TestObject {
        private final String value;

        public TestObject(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestObject that = (TestObject) obj;
            return value != null ? value.equals(that.value) : that.value == null;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }
}
