from dataclasses import dataclass
from datetime import datetime

@dataclass
class User:
    id: str
    username: str
    email: str
    password_hash: str
    created_at: datetime
    updated_at: datetime
    
    @classmethod
    def create(cls, username: str, email: str, password_hash: str):
        now = datetime.utcnow()
        return cls(
            id=None,  # This would typically be set by the database
            username=username,
            email=email,
            password_hash=password_hash,
            created_at=now,
            updated_at=now
        ) 