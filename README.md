# Vehicle Tracking System

A comprehensive system for tracking vehicle ownership and registration for the Rwanda Revenue Authority (RRA).

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Requirements](#system-requirements)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
- [Testing Guide](#testing-guide)
  - [Unit Testing](#unit-testing)
  - [Integration Testing](#integration-testing)
  - [API Testing](#api-testing)
  - [End-to-End Testing](#end-to-end-testing)
- [Authentication](#authentication)
- [Example API Calls](#example-api-calls)

## Overview

The Vehicle Tracking System is a Spring Boot application designed to manage vehicle registration, ownership, and transfers for the Rwanda Revenue Authority. It provides a RESTful API for administrators to manage vehicles, owners, and plate numbers.

## Features

- User authentication and authorization with JWT
- Vehicle registration and management
- Owner registration and management
- Plate number registration and management
- Vehicle ownership transfer tracking
- Search functionality for vehicles, owners, and plate numbers
- Validation for Rwandan National IDs, plate numbers, and chassis numbers

## System Requirements

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.8 or higher
- Postman or similar API testing tool (for testing)

## Setup Instructions

1. **Clone the repository**

```bash
git clone https://github.com/yourusername/vehicletracking.git
cd vehicletracking
```

2. **Configure the database**

Create a PostgreSQL database named `vehicle_tracking_rra`:

```bash
psql -U postgres
CREATE DATABASE vehicle_tracking_rra;
\q
```

3. **Configure application properties**

The default configuration is in `src/main/resources/application.properties`. Update the database credentials if needed:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vehicle_tracking_rra
spring.datasource.username=postgres
spring.datasource.password=yezu
```

4. **Build the application**

```bash
mvn clean install
```

5. **Run the application**

```bash
mvn spring-boot:run
```

6. **Initialize the admin user**

Make a GET request to `http://localhost:8080/api/auth/init-admin` to create the default admin user.

## API Documentation

The API documentation is available via Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

This provides a comprehensive interface to explore and test all available endpoints.

## Testing Guide

### Unit Testing

Unit tests focus on testing individual components in isolation.

1. **Run all unit tests**

```bash
mvn test
```

2. **Create new unit tests**

Create test classes in the `src/test/java` directory following the naming convention `*Test.java`.

Example unit test for a service:

```java
@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    @Test
    void testRegisterOwner() {
        // Arrange
        OwnerDTO ownerDTO = new OwnerDTO();
        ownerDTO.setName("Test Owner");
        ownerDTO.setNationalId("1199012345678901");
        ownerDTO.setEmail("test@example.com");
        ownerDTO.setPhone("0781234567");
        ownerDTO.setAddress("Kigali, Rwanda");

        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName(ownerDTO.getName());
        owner.setNationalId(ownerDTO.getNationalId());
        owner.setEmail(ownerDTO.getEmail());
        owner.setPhone(ownerDTO.getPhone());
        owner.setAddress(ownerDTO.getAddress());

        when(ownerRepository.findByNationalId(ownerDTO.getNationalId())).thenReturn(Optional.empty());
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);

        // Act
        Owner result = ownerService.registerOwner(ownerDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ownerDTO.getName(), result.getName());
        assertEquals(ownerDTO.getNationalId(), result.getNationalId());
        
        // Verify
        verify(ownerRepository).findByNationalId(ownerDTO.getNationalId());
        verify(ownerRepository).save(any(Owner.class));
    }
}
```

### Integration Testing

Integration tests verify that different components work together correctly.

1. **Create an integration test**

```java
@SpringBootTest
@AutoConfigureMockMvc
class OwnerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OwnerService ownerService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRegisterOwner() throws Exception {
        // Arrange
        OwnerDTO ownerDTO = new OwnerDTO();
        ownerDTO.setName("Test Owner");
        ownerDTO.setNationalId("1199012345678901");
        ownerDTO.setEmail("test@example.com");
        ownerDTO.setPhone("0781234567");
        ownerDTO.setAddress("Kigali, Rwanda");

        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName(ownerDTO.getName());
        owner.setNationalId(ownerDTO.getNationalId());
        owner.setEmail(ownerDTO.getEmail());
        owner.setPhone(ownerDTO.getPhone());
        owner.setAddress(ownerDTO.getAddress());

        when(ownerService.registerOwner(any(OwnerDTO.class))).thenReturn(owner);

        // Act & Assert
        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Owner registered successfully"))
                .andExpect(jsonPath("$.data.ownerId").value(1))
                .andExpect(jsonPath("$.data.name").value(ownerDTO.getName()))
                .andExpect(jsonPath("$.data.nationalId").value(ownerDTO.getNationalId()));
    }
}
```

2. **Run integration tests**

```bash
mvn test -Dtest=*IntegrationTest
```

### API Testing

API tests verify the behavior of the API endpoints using tools like Postman or REST Assured.

1. **Setting up Postman for API testing**

   - Download and install Postman from [https://www.postman.com/downloads/](https://www.postman.com/downloads/)
   - Create a new collection named "Vehicle Tracking API"
   - Set up environment variables:
     - `baseUrl`: http://localhost:8080
     - `token`: (empty initially, will be filled after login)

2. **Authentication test**

   - Create a POST request to `{{baseUrl}}/api/auth/login`
   - Set the body to:
     ```json
     {
       "email": "admin@example.com",
       "password": "admin123"
     }
     ```
   - Add a test script to extract and store the token:
     ```javascript
     var jsonData = pm.response.json();
     pm.environment.set("token", jsonData.accessToken);
     ```

3. **Testing protected endpoints**

   - Create a GET request to `{{baseUrl}}/api/owners`
   - Add an Authorization header: `Bearer {{token}}`
   - Run the request and verify the response

4. **Automated API testing with REST Assured**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTest {

    @LocalServerPort
    private int port;

    private String baseUrl;
    private String token;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        
        // Get authentication token
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("admin@example.com");
        loginDTO.setPassword("admin123");
        
        token = given()
            .contentType(ContentType.JSON)
            .body(loginDTO)
            .when()
            .post(baseUrl + "/api/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("accessToken");
    }

    @Test
    void testGetOwners() {
        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get(baseUrl + "/api/owners")
            .then()
            .statusCode(200)
            .body("content", notNullValue());
    }
}
```

### End-to-End Testing

End-to-end tests verify the complete flow of the application.

1. **Vehicle registration and ownership transfer test**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VehicleE2ETest {

    @LocalServerPort
    private int port;

    private String baseUrl;
    private String token;
    private Long ownerId;
    private Long vehicleId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        
        // Get authentication token
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("admin@example.com");
        loginDTO.setPassword("admin123");
        
        token = given()
            .contentType(ContentType.JSON)
            .body(loginDTO)
            .when()
            .post(baseUrl + "/api/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("accessToken");
    }

    @Test
    void testVehicleRegistrationAndTransfer() {
        // 1. Register an owner
        OwnerDTO ownerDTO = new OwnerDTO();
        ownerDTO.setName("Test Owner");
        ownerDTO.setNationalId("1199012345678901");
        ownerDTO.setEmail("test@example.com");
        ownerDTO.setPhone("0781234567");
        ownerDTO.setAddress("Kigali, Rwanda");
        
        ownerId = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(ownerDTO)
            .when()
            .post(baseUrl + "/api/owners")
            .then()
            .statusCode(200)
            .extract()
            .path("data.ownerId");
        
        // 2. Register a vehicle
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setOwnerId(ownerId);
        vehicleDTO.setChassisNumber("ABC123456789");
        vehicleDTO.setManufacturer("Toyota");
        vehicleDTO.setModel("Corolla");
        vehicleDTO.setYear(2020);
        vehicleDTO.setColor("White");
        
        PlateNumberDTO plateNumberDTO = new PlateNumberDTO();
        plateNumberDTO.setPlateNumber("RAA123A");
        vehicleDTO.setPlateNumber(plateNumberDTO);
        
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(vehicleDTO)
            .when()
            .post(baseUrl + "/api/vehicles")
            .then()
            .statusCode(200);
        
        // 3. Search for the vehicle
        vehicleId = given()
            .header("Authorization", "Bearer " + token)
            .param("query", "RAA123A")
            .when()
            .get(baseUrl + "/api/vehicles/search")
            .then()
            .statusCode(200)
            .extract()
            .path("content[0].id");
        
        // 4. Register a new owner for transfer
        OwnerDTO newOwnerDTO = new OwnerDTO();
        newOwnerDTO.setName("New Owner");
        newOwnerDTO.setNationalId("1199087654321098");
        newOwnerDTO.setEmail("new@example.com");
        newOwnerDTO.setPhone("0789876543");
        newOwnerDTO.setAddress("Musanze, Rwanda");
        
        Long newOwnerId = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(newOwnerDTO)
            .when()
            .post(baseUrl + "/api/owners")
            .then()
            .statusCode(200)
            .extract()
            .path("data.ownerId");
        
        // 5. Transfer the vehicle
        VehicleTransferDTO transferDTO = new VehicleTransferDTO();
        transferDTO.setVehicleId(vehicleId);
        transferDTO.setNewOwnerId(newOwnerId);
        
        PlateNumberDTO newPlateNumberDTO = new PlateNumberDTO();
        newPlateNumberDTO.setPlateNumber("RAB456B");
        transferDTO.setNewPlateNumber(newPlateNumberDTO);
        
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(transferDTO)
            .when()
            .post(baseUrl + "/api/vehicles/transfer")
            .then()
            .statusCode(200);
        
        // 6. Verify ownership history
        given()
            .header("Authorization", "Bearer " + token)
            .param("identifier", "ABC123456789")
            .when()
            .get(baseUrl + "/api/vehicles/history")
            .then()
            .statusCode(200)
            .body("content.size()", equalTo(2));
    }
}
```

## Authentication

The system uses JWT (JSON Web Token) for authentication. To access protected endpoints:

1. **Login to get a token**

```
POST /api/auth/login
```

Request body:
```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

Response:
```json
{
  "userId": 1,
  "name": "Admin User",
  "email": "admin@example.com",
  "role": "ADMIN",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

2. **Use the token in subsequent requests**

Add the Authorization header to all requests:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

3. **Refresh the token when it expires**

```
POST /api/auth/refresh-token
```

Request body:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

## Example API Calls

### 1. Register a new owner

```
POST /api/owners
```

Request body:
```json
{
  "name": "John Doe",
  "nationalId": "1199012345678901",
  "email": "john.doe@example.com",
  "phone": "0781234567",
  "address": "Kigali, Rwanda"
}
```

### 2. Register a vehicle

```
POST /api/vehicles
```

Request body:
```json
{
  "ownerId": 1,
  "chassisNumber": "ABC123456789",
  "manufacturer": "Toyota",
  "model": "Corolla",
  "year": 2020,
  "color": "White",
  "plateNumber": {
    "plateNumber": "RAA123A"
  }
}
```

### 3. Search for a vehicle

```
GET /api/vehicles/search?query=RAA123A
```

### 4. Transfer vehicle ownership

```
POST /api/vehicles/transfer
```

Request body:
```json
{
  "vehicleId": 1,
  "newOwnerId": 2,
  "newPlateNumber": {
    "plateNumber": "RAB456B"
  }
}
```

### 5. Get vehicle ownership history

```
GET /api/vehicles/history?identifier=ABC123456789
```