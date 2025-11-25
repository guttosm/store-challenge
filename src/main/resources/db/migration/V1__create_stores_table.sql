-- Create stores table
CREATE TABLE IF NOT EXISTS stores (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(50) NOT NULL UNIQUE,
    address_name VARCHAR(200) NOT NULL,
    street VARCHAR(200),
    street2 VARCHAR(200),
    street3 VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    sap_storeid VARCHAR(20),
    complex_number VARCHAR(50),
    location_type VARCHAR(50),
    collection_point BOOLEAN DEFAULT FALSE,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    today_open VARCHAR(10),
    today_close VARCHAR(10),
    show_warning_message BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on latitude and longitude for efficient distance queries
CREATE INDEX IF NOT EXISTS idx_latitude_longitude ON stores(latitude, longitude);

-- Create index on uuid for fast lookups
CREATE INDEX IF NOT EXISTS idx_stores_uuid ON stores(uuid);

