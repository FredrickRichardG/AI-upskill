from typing import Optional, Tuple
from models.user import User
from utils.auth import hash_password, verify_password, create_access_token

class AuthenticationError(Exception):
    """Base class for authentication-related errors."""
    pass

class UserExistsError(AuthenticationError):
    """Raised when attempting to register with an existing username/email."""
    pass

class InvalidCredentialsError(AuthenticationError):
    """Raised when login credentials are invalid."""
    pass

def register_user(username: str, email: str, password: str) -> User:
    """
    Register a new user.
    
    Args:
        username: The desired username
        email: User's email address
        password: User's password (will be hashed)
        
    Returns:
        User: The created user object
        
    Raises:
        UserExistsError: If username or email already exists
    """
    # In a real application, you would check if username/email exists in database
    # For demonstration, we'll assume they don't exist
    
    # Hash the password
    password_hash = hash_password(password)
    
    # Create new user
    user = User.create(
        username=username,
        email=email,
        password_hash=password_hash
    )
    
    # In a real application, save the user to database here
    
    return user

def login_user(username: str, password: str) -> Tuple[User, str]:
    """
    Authenticate a user and return a JWT token.
    
    Args:
        username: User's username
        password: User's password
        
    Returns:
        Tuple[User, str]: Tuple of (user object, JWT access token)
        
    Raises:
        InvalidCredentialsError: If credentials are invalid
    """
    # In a real application, fetch user from database
    # For demonstration, we'll raise an error
    # Replace this with actual database lookup
    raise InvalidCredentialsError("User not found")
    
    # Verify password
    if not verify_password(password, user.password_hash):
        raise InvalidCredentialsError("Invalid password")
    
    # Create access token
    token_data = {
        "sub": str(user.id),
        "username": user.username
    }
    access_token = create_access_token(token_data)
    
    return user, access_token 