-- Add emergency column to iot_device_locations (per-event)
ALTER TABLE iot_device_locations
    ADD COLUMN emergency VARCHAR(10) NULL;

-- Add last_emergency snapshot to iot_devices
ALTER TABLE iot_devices
    ADD COLUMN last_emergency VARCHAR(10) NULL;
