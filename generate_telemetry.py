import csv
from datetime import datetime, timedelta
import math
import random

def generate_telemetry():
    # Initialize file
    with open('telemetry.csv', 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['DeviceId', 'Timestamp', 'AmbientTemperature', 'DeviceTemperature'])
        
        # Set time range
        start_time = datetime(2024, 2, 2, 0, 0, 0)  # Start at midnight
        end_time = start_time + timedelta(days=2)
        current_time = start_time
        
        # Device parameters
        device_id = "DEVICE_001"
        base_device_temp = 50.0  # Base device operating temperature
        
        # Generate data for each second
        while current_time < end_time:
            # Calculate ambient temperature with daily cycle
            hour = current_time.hour
            # Daily cycle: coolest at 4AM (18°C), warmest at 4PM (26°C)
            base_ambient = 22 + 4 * math.sin(((hour - 4) / 24) * 2 * math.pi)
            
            # Add some random variation to ambient temp (±0.5°C)
            ambient_temp = round(base_ambient + random.uniform(-0.5, 0.5), 2)
            
            # Device temperature is affected by ambient temperature
            # and has occasional spikes
            ambient_effect = (ambient_temp - 22) * 1.5  # Ambient temperature influence
            
            # Random spikes (0.1% chance of spike)
            spike = random.uniform(5, 15) if random.random() < 0.001 else 0
            
            # Calculate device temperature with some noise
            device_temp = round(base_device_temp + ambient_effect + spike + random.uniform(-0.5, 0.5), 2)
            
            # Write the record
            writer.writerow([
                device_id,
                current_time.strftime("%Y-%m-%dT%H:%M:%SZ"),
                ambient_temp,
                device_temp
            ])
            
            # Move to next second
            current_time += timedelta(seconds=1)

if __name__ == "__main__":
    print("Generating telemetry data...")
    generate_telemetry()
    print("Done! Generated telemetry.csv with 2 days of data (172,800 records)")
