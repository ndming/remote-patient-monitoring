import enum

RPMSOS0000_CERTI_PATH = 'auth/RPMSOS0000/69a713daa8a9d05b477ea0172939360fff01619c064b7720f2ec5882503a79ee-certificate.pem.crt'
RPMSOS0000_KEY_PATH = 'auth/RPMSOS0000/69a713daa8a9d05b477ea0172939360fff01619c064b7720f2ec5882503a79ee-private.pem.key'

RPMSOS0001_CERTI_PATH = 'auth/RPMSOS0001/335e60492169df4893484de4644741ddd52dbe1abdc4cc54e1c1e060933605d7-certificate.pem.crt'
RPMSOS0001_KEY_PATH = 'auth/RPMSOS0001/335e60492169df4893484de4644741ddd52dbe1abdc4cc54e1c1e060933605d7-private.pem.key'

class Credentials:
    def __init__(self, certi_path: str, key_path: str) -> None:
        self.certi_path = certi_path
        self.key_path = key_path

class Target(enum.Enum):
    RPMSOS0000 = 0
    RPMSOS0001 = 1
    
    @classmethod
    def getCredentials(self, name: str) -> Credentials:
        if (name == Target.RPMSOS0000.name):
            return Credentials(RPMSOS0000_CERTI_PATH, RPMSOS0000_KEY_PATH)
        elif (name == Target.RPMSOS0001.name):
            return Credentials(RPMSOS0001_CERTI_PATH, RPMSOS0001_KEY_PATH)
        else:
            return None