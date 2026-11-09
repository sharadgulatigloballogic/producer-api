# User API Producer

A simple Spring Boot REST API for user management.

## Build and Run

```bash
./mvnw clean install
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Endpoints

### Get All Users
```bash
GET /api/users
```

### Get Active Users Only
```bash
GET /api/users/active
```

### Search Users
```bash
GET /api/users/search?q={text}&active={true|false}
```
`q` is required (400 if missing or blank). `active` is optional and filters by active status.
Matches `username`, `email` or `fullName` as a case-insensitive substring.
Returns `200` with a list of matching users, or `[]` if none match.

### Get User by ID
```bash
GET /api/users/{id}
```

### Get User by Username
```bash
GET /api/users/username/{username}
```

### Create User
```bash
POST /api/users
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@example.com",
  "fullName": "New User"
}
```

### Update User
```bash
PUT /api/users/{id}
Content-Type: application/json

{
  "username": "updateduser",
  "email": "updated@example.com",
  "fullName": "Updated Name"
}
```

### Delete User
```bash
DELETE /api/users/{id}
```

### Deactivate User
```bash
PATCH /api/users/{id}/deactivate
```

### Activate User
```bash
PATCH /api/users/{id}/activate
```

## Testing

```bash
# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1

# Search users by text (optionally only active/inactive ones)
curl "http://localhost:8080/api/users/search?q=john"
curl "http://localhost:8080/api/users/search?q=doe&active=true"

# Create a new user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","fullName":"Test User"}'
```
