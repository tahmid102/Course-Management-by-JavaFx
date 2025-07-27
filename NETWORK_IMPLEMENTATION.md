# Network Implementation for Student Management System

## Overview

This implementation enhances the existing Student Management System with network-based communication between clients and a centralized database server. The system now supports:

1. **Network-based data loading** - Clients load student/teacher/course data from a central server
2. **Network registration workflow** - User registrations are submitted to the server
3. **Admin approval via network** - Admins can approve registrations through the server
4. **Fallback to local files** - System works offline if server is unavailable

## Architecture

### Core Components

1. **NetworkLoader** (`files.Classes.NetworkLoader`)
   - Main client-side class for network communication
   - Handles data loading, registration submission, and approval requests
   - Provides fallback to local file operations

2. **DatabaseServer** (`files.Server.DatabaseServer`)
   - Central server that manages all database operations
   - Handles multiple client connections concurrently
   - Processes data requests, registrations, and approvals

3. **Enhanced SocketWrapper** (`files.Server.SocketWrapper`)
   - Improved networking layer with better error handling
   - Supports request-response patterns
   - Automatic connection management

4. **Data Communication Classes**
   - `DataPacket` - Generic data transfer container
   - `RegistrationRequest` - Structured registration data
   - `FilePacket` - File transfer support (existing)

## Key Features

### 1. Network-Based Data Loading

The system now loads data from the server instead of local files:

```java
// Try network first, fallback to local
if (NetworkLoader.isServerAvailable()) {
    NetworkLoader.loadAll(); // Loads from server
} else {
    Loader.loadAll(); // Fallback to local files
}
```

### 2. Network Registration Workflow

User registrations are now submitted to the server:

```java
RegistrationRequest request = new RegistrationRequest(name, id, password, userType);
boolean success = NetworkLoader.submitRegistration(request);
```

### 3. Admin Approval System

Admins can approve registrations which automatically:
- Removes user from pending database
- Adds user to main database
- Creates user object in memory

```java
boolean success = NetworkLoader.approveRegistration(userId, "student");
```

### 4. Data Synchronization

Server can reload data from files:

```java
boolean success = NetworkLoader.syncDataWithServer();
```

## Usage Instructions

### Starting the System

1. **Start the Database Server**
   ```bash
   java files.Server.DatabaseServer
   ```
   - Server will start on port 44444
   - Loads all data from database files
   - Ready to accept client connections

2. **Run the Client Application**
   - Start your JavaFX application normally
   - The system will automatically try to connect to the server
   - If server is unavailable, it falls back to local file operations

3. **Test the Network Features**
   ```bash
   java files.Test.NetworkTestClient
   ```

### Registration Flow

1. **User Registration**
   - User fills out registration form
   - System tries to submit to server first
   - If server unavailable, saves to local pending file
   - User gets confirmation message

2. **Admin Approval**
   - Admin opens approval dashboard
   - System loads pending registrations from server
   - Admin approves/rejects users
   - Approved users are moved to main database

### Configuration

The network settings can be modified in `NetworkLoader`:

```java
private static final String SERVER_HOST = "localhost";
private static final int SERVER_PORT = 44444;
```

## File Structure

### Database Files (Enhanced)
- `database/StudentCredentials.txt` - Approved students
- `database/TeacherCredentials.txt` - Approved teachers
- `database/Courses.txt` - Course data
- `database/pendingStudentCredentials.txt` - Pending student registrations
- `database/pendingTeacherCredentials.txt` - Pending teacher registrations
- `database/enrollments.txt` - Course enrollments
- `database/AssignedCoursesTeacher.txt` - Teacher course assignments

### Network Classes
```
src/main/java/files/
├── Classes/
│   ├── NetworkLoader.java          # Client-side network operations
│   └── [existing classes...]
├── Server/
│   ├── DatabaseServer.java         # Main database server
│   ├── DataPacket.java            # Data transfer container
│   ├── RegistrationRequest.java   # Registration data structure
│   ├── SocketWrapper.java         # Enhanced networking
│   └── [existing server classes...]
└── Test/
    └── NetworkTestClient.java     # Testing utility
```

## Benefits

1. **Centralized Data Management**
   - All data is managed by a central server
   - Consistent data across all clients
   - Real-time updates

2. **Improved Registration Workflow**
   - Immediate validation against server data
   - Centralized approval process
   - No file conflicts

3. **Scalability**
   - Multiple clients can connect simultaneously
   - Server handles concurrent requests
   - Thread-safe operations

4. **Reliability**
   - Automatic fallback to local files
   - Robust error handling
   - Connection timeout management

5. **Seamless Integration**
   - Existing code mostly unchanged
   - Gradual migration path
   - Backward compatibility

## Error Handling

The system includes comprehensive error handling:

- **Connection failures** - Automatic fallback to local files
- **Server timeouts** - 10-second timeout with retry logic
- **Data corruption** - Validation and error reporting
- **Concurrent access** - Thread-safe server operations

## Future Enhancements

Potential improvements:
1. **Authentication** - Add user authentication for server access
2. **Encryption** - Secure data transmission
3. **Database Integration** - Use actual database instead of text files
4. **Real-time Updates** - Push notifications for data changes
5. **Load Balancing** - Multiple server instances
6. **Web API** - REST API for broader client support

## Troubleshooting

### Common Issues

1. **Server not starting**
   - Check if port 44444 is available
   - Ensure database files exist and are readable

2. **Client connection failures**
   - Verify server is running
   - Check network connectivity
   - Confirm firewall settings

3. **Data not loading**
   - Check server logs for errors
   - Verify file permissions
   - Ensure data files are not corrupted

### Debug Mode

Enable debug logging by adding print statements in NetworkLoader methods to trace network operations.

## Testing

Use the `NetworkTestClient` to verify system functionality:

```bash
# Start server first
java files.Server.DatabaseServer

# In another terminal, run test
java files.Test.NetworkTestClient
```

The test will verify:
- Server connectivity
- Data loading
- Registration submission
- Data synchronization

## Conclusion

This network implementation provides a robust foundation for scaling the Student Management System while maintaining backward compatibility. The system gracefully handles both networked and offline scenarios, ensuring reliable operation in various environments.