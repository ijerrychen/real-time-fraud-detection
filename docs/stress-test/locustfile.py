from locust import HttpUser, task, between
import random
import time
import string

class FraudDetectionUser(HttpUser):
    wait_time = between(0.1, 0.5)
    
    @task(10)
    def send_valid_transaction(self):
        account_id = f"ACCT{random.randint(10000, 99999)}"
        payload = {
            "transactionId": f"TXN-{int(time.time()*1000)}",
            "accountId": account_id,
            "amount": round(random.uniform(10, 500), 2),
            "merchant": random.choice(["Amazon", "Walmart", "Apple", "Google"]),
            "timestamp": int(time.time())
        }
        self.client.post(
            "/transactions", 
            json=payload,
            headers={"Content-Type": "application/json"}
        )
    
    @task(2)
    def send_high_value_transaction(self):
        payload = {
            "transactionId": f"TXN-HIGH-{int(time.time()*1000)}",
            "accountId": f"ACCT{random.randint(10000, 99999)}",
            "amount": round(random.uniform(5000, 10000), 2),
            "merchant": "LuxuryStore",
            "timestamp": int(time.time())
        }
        self.client.post(
            "/transactions", 
            json=payload,
            headers={"Content-Type": "application/json"}
        )
    
    @task(1)
    def send_invalid_transaction(self):
        payload = {
            "transactionId": "",
            "accountId": "ACCT123",
            "amount": -100,
            "merchant": "",
            "timestamp": 0
        }
        self.client.post(
            "/transactions", 
            json=payload,
            headers={"Content-Type": "application/json"}
        )