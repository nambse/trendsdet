#!/usr/bin/env python3
"""
Populate Shopify store with quality products and collections for Trendsdet app.
"""

import requests
import json
import time
import sys
import os

SHOP_DOMAIN = os.environ.get("SHOPIFY_ADMIN_DOMAIN", "sefadevtest.myshopify.com")
ACCESS_TOKEN = os.environ["SHOPIFY_ADMIN_TOKEN"]  # Required: Shopify Admin API access token
API_VERSION = "2024-10"
BASE_URL = f"https://{SHOP_DOMAIN}/admin/api/{API_VERSION}"

HEADERS = {
    "X-Shopify-Access-Token": ACCESS_TOKEN,
    "Content-Type": "application/json",
}

def api_get(endpoint, params=None):
    r = requests.get(f"{BASE_URL}/{endpoint}", headers=HEADERS, params=params)
    r.raise_for_status()
    return r.json()

def api_post(endpoint, data):
    r = requests.post(f"{BASE_URL}/{endpoint}", headers=HEADERS, json=data)
    if r.status_code >= 400:
        print(f"  ERROR {r.status_code}: {r.text[:300]}")
    r.raise_for_status()
    return r.json()

def api_put(endpoint, data):
    r = requests.put(f"{BASE_URL}/{endpoint}", headers=HEADERS, json=data)
    if r.status_code >= 400:
        print(f"  ERROR {r.status_code}: {r.text[:300]}")
    r.raise_for_status()
    return r.json()

def api_delete(endpoint):
    r = requests.delete(f"{BASE_URL}/{endpoint}", headers=HEADERS)
    return r.status_code

# ── Step 1: Delete all existing products ──────────────────────────────

def delete_all_products():
    print("=== Deleting all existing products ===")
    page = 1
    while True:
        data = api_get("products.json", {"limit": 250, "fields": "id"})
        products = data.get("products", [])
        if not products:
            break
        for p in products:
            api_delete(f"products/{p['id']}.json")
        print(f"  Deleted {len(products)} products")
        time.sleep(0.5)
    print("  All products deleted.\n")

def delete_custom_collections():
    print("=== Deleting custom collections ===")
    data = api_get("custom_collections.json", {"limit": 250})
    for c in data.get("custom_collections", []):
        api_delete(f"custom_collections/{c['id']}.json")
        print(f"  Deleted collection: {c['title']}")
    print()

def delete_smart_collections():
    print("=== Deleting smart collections ===")
    data = api_get("smart_collections.json", {"limit": 250})
    for c in data.get("smart_collections", []):
        api_delete(f"smart_collections/{c['id']}.json")
        print(f"  Deleted smart collection: {c['title']}")
    print()

# ── Step 2: Create products ──────────────────────────────────────────

# High-quality free images from picsum/unsplash-style URLs
PRODUCTS = [
    # ── Electronics ──
    {
        "title": "Wireless Noise-Canceling Headphones",
        "body_html": "<p>Premium over-ear headphones with active noise cancellation and 30-hour battery life. Features Bluetooth 5.3, multi-device pairing, and memory foam ear cushions for all-day comfort. Perfect for music lovers and professionals alike.</p>",
        "vendor": "TrendAudio",
        "product_type": "Electronics",
        "tags": "electronics, headphones, wireless, noise-canceling, bluetooth",
        "variants": [
            {"title": "Midnight Black", "price": "199.99", "compare_at_price": "249.99", "sku": "WH-BLK-001", "inventory_quantity": 50, "option1": "Midnight Black"},
            {"title": "Pearl White", "price": "199.99", "compare_at_price": "249.99", "sku": "WH-WHT-001", "inventory_quantity": 35, "option1": "Pearl White"},
            {"title": "Navy Blue", "price": "209.99", "compare_at_price": "259.99", "sku": "WH-BLU-001", "inventory_quantity": 20, "option1": "Navy Blue"},
        ],
        "options": [{"name": "Color"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80"},
            {"src": "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800&q=80"},
        ],
    },
    {
        "title": "Smart Watch Pro",
        "body_html": "<p>Advanced fitness and health tracking smartwatch with AMOLED display, GPS, heart rate monitor, blood oxygen sensor, and 7-day battery life. Water resistant to 50 meters. Compatible with iOS and Android.</p>",
        "vendor": "TrendTech",
        "product_type": "Electronics",
        "tags": "electronics, smartwatch, fitness, health, wearable",
        "variants": [
            {"title": "40mm / Black Sport Band", "price": "299.99", "compare_at_price": "349.99", "sku": "SW-40-BLK", "inventory_quantity": 40, "option1": "40mm", "option2": "Black Sport Band"},
            {"title": "40mm / White Sport Band", "price": "299.99", "compare_at_price": "349.99", "sku": "SW-40-WHT", "inventory_quantity": 25, "option1": "40mm", "option2": "White Sport Band"},
            {"title": "44mm / Black Sport Band", "price": "329.99", "compare_at_price": "379.99", "sku": "SW-44-BLK", "inventory_quantity": 30, "option1": "44mm", "option2": "Black Sport Band"},
            {"title": "44mm / Silver Mesh", "price": "359.99", "compare_at_price": "399.99", "sku": "SW-44-SLV", "inventory_quantity": 15, "option1": "44mm", "option2": "Silver Mesh"},
        ],
        "options": [{"name": "Size"}, {"name": "Band"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80"},
            {"src": "https://images.unsplash.com/photo-1546868871-af0de0ae72be?w=800&q=80"},
        ],
    },
    {
        "title": "Portable Bluetooth Speaker",
        "body_html": "<p>Compact waterproof Bluetooth speaker with 360-degree sound, deep bass, and 12-hour playtime. IPX7 waterproof rating makes it perfect for pool parties, beach trips, and outdoor adventures.</p>",
        "vendor": "TrendAudio",
        "product_type": "Electronics",
        "tags": "electronics, speaker, bluetooth, portable, waterproof",
        "variants": [
            {"title": "Ocean Blue", "price": "79.99", "compare_at_price": None, "sku": "BS-BLU-001", "inventory_quantity": 100, "option1": "Ocean Blue"},
            {"title": "Sunset Orange", "price": "79.99", "compare_at_price": None, "sku": "BS-ORG-001", "inventory_quantity": 80, "option1": "Sunset Orange"},
            {"title": "Forest Green", "price": "79.99", "compare_at_price": None, "sku": "BS-GRN-001", "inventory_quantity": 60, "option1": "Forest Green"},
        ],
        "options": [{"name": "Color"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=800&q=80"},
        ],
    },
    {
        "title": "Wireless Charging Pad",
        "body_html": "<p>Ultra-slim 15W fast wireless charging pad compatible with all Qi-enabled devices. Features LED indicator, anti-slip surface, and foreign object detection for safe charging.</p>",
        "vendor": "TrendTech",
        "product_type": "Electronics",
        "tags": "electronics, charging, wireless, accessories",
        "variants": [
            {"title": "Default", "price": "29.99", "compare_at_price": "39.99", "sku": "WC-001", "inventory_quantity": 200, "option1": "Default Title"},
        ],
        "options": [{"name": "Title"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=800&q=80"},
        ],
    },
    # ── Clothing ──
    {
        "title": "Premium Cotton T-Shirt",
        "body_html": "<p>Ultra-soft 100% organic cotton t-shirt with a relaxed fit. Pre-shrunk fabric, reinforced seams, and tagless comfort label. Available in multiple colors and sizes for everyday style.</p>",
        "vendor": "TrendWear",
        "product_type": "Clothing",
        "tags": "clothing, t-shirt, cotton, organic, casual",
        "variants": [
            {"title": "S / White", "price": "29.99", "compare_at_price": None, "sku": "TS-S-WHT", "inventory_quantity": 100, "option1": "S", "option2": "White"},
            {"title": "M / White", "price": "29.99", "compare_at_price": None, "sku": "TS-M-WHT", "inventory_quantity": 150, "option1": "M", "option2": "White"},
            {"title": "L / White", "price": "29.99", "compare_at_price": None, "sku": "TS-L-WHT", "inventory_quantity": 120, "option1": "L", "option2": "White"},
            {"title": "M / Black", "price": "29.99", "compare_at_price": None, "sku": "TS-M-BLK", "inventory_quantity": 130, "option1": "M", "option2": "Black"},
            {"title": "L / Black", "price": "29.99", "compare_at_price": None, "sku": "TS-L-BLK", "inventory_quantity": 110, "option1": "L", "option2": "Black"},
        ],
        "options": [{"name": "Size"}, {"name": "Color"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=800&q=80"},
            {"src": "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=800&q=80"},
        ],
    },
    {
        "title": "Classic Denim Jacket",
        "body_html": "<p>Timeless denim jacket crafted from premium selvedge denim. Features brass buttons, adjustable waist tabs, and a modern slim fit. A wardrobe essential that gets better with age.</p>",
        "vendor": "TrendWear",
        "product_type": "Clothing",
        "tags": "clothing, jacket, denim, classic, outerwear",
        "variants": [
            {"title": "S", "price": "89.99", "compare_at_price": "119.99", "sku": "DJ-S-001", "inventory_quantity": 30, "option1": "S"},
            {"title": "M", "price": "89.99", "compare_at_price": "119.99", "sku": "DJ-M-001", "inventory_quantity": 45, "option1": "M"},
            {"title": "L", "price": "89.99", "compare_at_price": "119.99", "sku": "DJ-L-001", "inventory_quantity": 40, "option1": "L"},
            {"title": "XL", "price": "89.99", "compare_at_price": "119.99", "sku": "DJ-XL-001", "inventory_quantity": 20, "option1": "XL"},
        ],
        "options": [{"name": "Size"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800&q=80"},
            {"src": "https://images.unsplash.com/photo-1495105787522-5334e3ffa0ef?w=800&q=80"},
        ],
    },
    {
        "title": "Running Sneakers",
        "body_html": "<p>Lightweight performance running shoes with responsive cushioning and breathable mesh upper. Engineered for comfort on long runs with arch support and shock absorption technology.</p>",
        "vendor": "TrendSport",
        "product_type": "Clothing",
        "tags": "clothing, shoes, sneakers, running, sport",
        "variants": [
            {"title": "US 8 / Gray", "price": "129.99", "compare_at_price": "159.99", "sku": "RS-8-GRY", "inventory_quantity": 25, "option1": "US 8", "option2": "Gray"},
            {"title": "US 9 / Gray", "price": "129.99", "compare_at_price": "159.99", "sku": "RS-9-GRY", "inventory_quantity": 35, "option1": "US 9", "option2": "Gray"},
            {"title": "US 10 / Gray", "price": "129.99", "compare_at_price": "159.99", "sku": "RS-10-GRY", "inventory_quantity": 40, "option1": "US 10", "option2": "Gray"},
            {"title": "US 10 / Black", "price": "129.99", "compare_at_price": "159.99", "sku": "RS-10-BLK", "inventory_quantity": 30, "option1": "US 10", "option2": "Black"},
        ],
        "options": [{"name": "Size"}, {"name": "Color"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&q=80"},
            {"src": "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800&q=80"},
        ],
    },
    {
        "title": "Wool Blend Scarf",
        "body_html": "<p>Luxuriously soft wool-cashmere blend scarf with classic herringbone pattern. Generously sized at 180cm x 30cm for versatile styling. Perfect for adding warmth and elegance to any outfit.</p>",
        "vendor": "TrendWear",
        "product_type": "Clothing",
        "tags": "clothing, scarf, wool, accessories, winter",
        "variants": [
            {"title": "Camel", "price": "49.99", "compare_at_price": None, "sku": "SC-CML-001", "inventory_quantity": 70, "option1": "Camel"},
            {"title": "Charcoal", "price": "49.99", "compare_at_price": None, "sku": "SC-CHR-001", "inventory_quantity": 60, "option1": "Charcoal"},
            {"title": "Burgundy", "price": "49.99", "compare_at_price": None, "sku": "SC-BRG-001", "inventory_quantity": 45, "option1": "Burgundy"},
        ],
        "options": [{"name": "Color"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1520903920243-00d872a2d1c9?w=800&q=80"},
        ],
    },
    # ── Home & Living ──
    {
        "title": "Scented Soy Candle Set",
        "body_html": "<p>Hand-poured soy wax candles in artisan ceramic jars. Set of 3 complementary scents: Vanilla Bean, Fresh Linen, and Mediterranean Fig. Each candle burns for 45+ hours with a clean, even flame.</p>",
        "vendor": "TrendHome",
        "product_type": "Home & Living",
        "tags": "home, candles, soy, scented, decor",
        "variants": [
            {"title": "Set of 3", "price": "44.99", "compare_at_price": "59.99", "sku": "SC-SET3-001", "inventory_quantity": 80, "option1": "Set of 3"},
            {"title": "Single - Vanilla Bean", "price": "18.99", "compare_at_price": None, "sku": "SC-VAN-001", "inventory_quantity": 120, "option1": "Single - Vanilla Bean"},
            {"title": "Single - Fresh Linen", "price": "18.99", "compare_at_price": None, "sku": "SC-LIN-001", "inventory_quantity": 100, "option1": "Single - Fresh Linen"},
        ],
        "options": [{"name": "Size"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1602028915047-37269d1a73f7?w=800&q=80"},
            {"src": "https://images.unsplash.com/photo-1603006905003-be475563bc59?w=800&q=80"},
        ],
    },
    {
        "title": "Ceramic Coffee Mug",
        "body_html": "<p>Handcrafted ceramic mug with a matte finish and comfortable handle. Holds 12oz of your favorite beverage. Microwave and dishwasher safe. Each piece is unique with subtle glaze variations.</p>",
        "vendor": "TrendHome",
        "product_type": "Home & Living",
        "tags": "home, mug, ceramic, coffee, kitchen",
        "variants": [
            {"title": "Sage Green", "price": "19.99", "compare_at_price": None, "sku": "MG-SGN-001", "inventory_quantity": 90, "option1": "Sage Green"},
            {"title": "Dusty Rose", "price": "19.99", "compare_at_price": None, "sku": "MG-DRS-001", "inventory_quantity": 85, "option1": "Dusty Rose"},
            {"title": "Slate Blue", "price": "19.99", "compare_at_price": None, "sku": "MG-SBL-001", "inventory_quantity": 75, "option1": "Slate Blue"},
            {"title": "Cream", "price": "19.99", "compare_at_price": None, "sku": "MG-CRM-001", "inventory_quantity": 100, "option1": "Cream"},
        ],
        "options": [{"name": "Color"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?w=800&q=80"},
        ],
    },
    {
        "title": "Minimalist Wall Clock",
        "body_html": "<p>Modern minimalist wall clock with silent sweep mechanism. 12-inch diameter with clean numerical design. Precision quartz movement ensures accurate timekeeping. A stylish addition to any room.</p>",
        "vendor": "TrendHome",
        "product_type": "Home & Living",
        "tags": "home, clock, wall, minimalist, decor",
        "variants": [
            {"title": "White / Gold", "price": "39.99", "compare_at_price": "54.99", "sku": "WC-WGD-001", "inventory_quantity": 45, "option1": "White / Gold"},
            {"title": "Black / Silver", "price": "39.99", "compare_at_price": "54.99", "sku": "WC-BSV-001", "inventory_quantity": 40, "option1": "Black / Silver"},
        ],
        "options": [{"name": "Style"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1563861826100-9cb868fdbe1c?w=800&q=80"},
        ],
    },
    {
        "title": "Linen Throw Pillow",
        "body_html": "<p>Premium stonewashed linen throw pillow with hidden zipper closure. 18x18 inches, filled with hypoallergenic down-alternative insert. Adds texture and warmth to any sofa or bed.</p>",
        "vendor": "TrendHome",
        "product_type": "Home & Living",
        "tags": "home, pillow, linen, decor, living room",
        "variants": [
            {"title": "Natural Beige", "price": "34.99", "compare_at_price": None, "sku": "TP-NBG-001", "inventory_quantity": 65, "option1": "Natural Beige"},
            {"title": "Olive Green", "price": "34.99", "compare_at_price": None, "sku": "TP-OGN-001", "inventory_quantity": 55, "option1": "Olive Green"},
            {"title": "Terracotta", "price": "34.99", "compare_at_price": None, "sku": "TP-TRC-001", "inventory_quantity": 50, "option1": "Terracotta"},
        ],
        "options": [{"name": "Color"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=800&q=80"},
        ],
    },
    # ── Beauty & Personal Care ──
    {
        "title": "Vitamin C Serum",
        "body_html": "<p>Professional-grade 20% Vitamin C serum with Hyaluronic Acid and Vitamin E. Brightens skin, reduces dark spots, and boosts collagen production. Suitable for all skin types. 30ml dropper bottle.</p>",
        "vendor": "TrendBeauty",
        "product_type": "Beauty",
        "tags": "beauty, skincare, serum, vitamin c, anti-aging",
        "variants": [
            {"title": "30ml", "price": "24.99", "compare_at_price": "34.99", "sku": "VCS-30-001", "inventory_quantity": 150, "option1": "30ml"},
            {"title": "60ml", "price": "39.99", "compare_at_price": "54.99", "sku": "VCS-60-001", "inventory_quantity": 80, "option1": "60ml"},
        ],
        "options": [{"name": "Size"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80"},
        ],
    },
    {
        "title": "Natural Lip Balm Set",
        "body_html": "<p>Set of 4 organic lip balms made with beeswax, coconut oil, and shea butter. Flavors include Honey, Mint, Berry, and Vanilla. Moisturizes and protects lips all day long.</p>",
        "vendor": "TrendBeauty",
        "product_type": "Beauty",
        "tags": "beauty, lip balm, organic, natural, skincare",
        "variants": [
            {"title": "Set of 4", "price": "14.99", "compare_at_price": "19.99", "sku": "LB-SET4-001", "inventory_quantity": 200, "option1": "Set of 4"},
        ],
        "options": [{"name": "Title"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=800&q=80"},
        ],
    },
    {
        "title": "Jade Face Roller",
        "body_html": "<p>Authentic jade stone face roller with dual-ended design for face and under-eye massage. Reduces puffiness, promotes circulation, and enhances skincare product absorption. Comes in a velvet storage pouch.</p>",
        "vendor": "TrendBeauty",
        "product_type": "Beauty",
        "tags": "beauty, face roller, jade, skincare, wellness",
        "variants": [
            {"title": "Green Jade", "price": "22.99", "compare_at_price": None, "sku": "JR-GRN-001", "inventory_quantity": 90, "option1": "Green Jade"},
            {"title": "Rose Quartz", "price": "24.99", "compare_at_price": None, "sku": "JR-RSQ-001", "inventory_quantity": 70, "option1": "Rose Quartz"},
        ],
        "options": [{"name": "Stone"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1590439471364-192aa70c0b53?w=800&q=80"},
        ],
    },
    # ── Bags & Accessories ──
    {
        "title": "Leather Crossbody Bag",
        "body_html": "<p>Handcrafted genuine leather crossbody bag with adjustable strap. Features two main compartments, interior zip pocket, and magnetic snap closure. Compact yet spacious enough for daily essentials.</p>",
        "vendor": "TrendAccessories",
        "product_type": "Bags",
        "tags": "bags, leather, crossbody, accessories, fashion",
        "variants": [
            {"title": "Cognac", "price": "79.99", "compare_at_price": "99.99", "sku": "CB-COG-001", "inventory_quantity": 35, "option1": "Cognac"},
            {"title": "Black", "price": "79.99", "compare_at_price": "99.99", "sku": "CB-BLK-001", "inventory_quantity": 40, "option1": "Black"},
            {"title": "Tan", "price": "79.99", "compare_at_price": "99.99", "sku": "CB-TAN-001", "inventory_quantity": 25, "option1": "Tan"},
        ],
        "options": [{"name": "Color"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=800&q=80"},
            {"src": "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=800&q=80"},
        ],
    },
    {
        "title": "Canvas Tote Bag",
        "body_html": "<p>Durable organic cotton canvas tote bag with reinforced handles and interior pocket. Perfect for groceries, beach trips, or everyday carry. Machine washable and eco-friendly.</p>",
        "vendor": "TrendAccessories",
        "product_type": "Bags",
        "tags": "bags, canvas, tote, eco-friendly, organic",
        "variants": [
            {"title": "Natural", "price": "24.99", "compare_at_price": None, "sku": "TB-NAT-001", "inventory_quantity": 150, "option1": "Natural"},
            {"title": "Black", "price": "24.99", "compare_at_price": None, "sku": "TB-BLK-001", "inventory_quantity": 120, "option1": "Black"},
        ],
        "options": [{"name": "Color"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1544816155-12df9643f363?w=800&q=80"},
        ],
    },
    {
        "title": "Minimalist Watch",
        "body_html": "<p>Elegant minimalist watch with Japanese quartz movement and genuine leather strap. 38mm stainless steel case with scratch-resistant sapphire crystal. Water resistant to 30 meters.</p>",
        "vendor": "TrendAccessories",
        "product_type": "Bags",
        "tags": "accessories, watch, minimalist, leather, fashion",
        "variants": [
            {"title": "Silver / Brown Strap", "price": "149.99", "compare_at_price": "189.99", "sku": "MW-SBR-001", "inventory_quantity": 20, "option1": "Silver / Brown Strap"},
            {"title": "Gold / Black Strap", "price": "159.99", "compare_at_price": "199.99", "sku": "MW-GBK-001", "inventory_quantity": 15, "option1": "Gold / Black Strap"},
            {"title": "Rose Gold / Pink Strap", "price": "159.99", "compare_at_price": "199.99", "sku": "MW-RGP-001", "inventory_quantity": 18, "option1": "Rose Gold / Pink Strap"},
        ],
        "options": [{"name": "Style"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=800&q=80"},
            {"src": "https://images.unsplash.com/photo-1522312346375-d1a52e2b99b3?w=800&q=80"},
        ],
    },
    {
        "title": "Sunglasses UV400",
        "body_html": "<p>Classic aviator sunglasses with UV400 protection and polarized lenses. Lightweight metal frame with adjustable nose pads for a comfortable fit. Includes hard shell carrying case and cleaning cloth.</p>",
        "vendor": "TrendAccessories",
        "product_type": "Bags",
        "tags": "accessories, sunglasses, uv protection, fashion, summer",
        "variants": [
            {"title": "Gold / Green", "price": "59.99", "compare_at_price": None, "sku": "SG-GGN-001", "inventory_quantity": 55, "option1": "Gold / Green"},
            {"title": "Silver / Blue", "price": "59.99", "compare_at_price": None, "sku": "SG-SBL-001", "inventory_quantity": 45, "option1": "Silver / Blue"},
            {"title": "Black / Gray", "price": "59.99", "compare_at_price": None, "sku": "SG-BGY-001", "inventory_quantity": 60, "option1": "Black / Gray"},
        ],
        "options": [{"name": "Style"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=800&q=80"},
        ],
    },
    # ── Books & Stationery ──
    {
        "title": "Leather Bound Journal",
        "body_html": "<p>Handmade leather journal with 240 pages of acid-free cream paper. Features a wrap-around closure, bookmark ribbon, and back pocket. Perfect for journaling, sketching, or note-taking.</p>",
        "vendor": "TrendStationery",
        "product_type": "Stationery",
        "tags": "stationery, journal, leather, notebook, writing",
        "variants": [
            {"title": "A5 / Brown", "price": "32.99", "compare_at_price": None, "sku": "LJ-A5-BRN", "inventory_quantity": 80, "option1": "A5", "option2": "Brown"},
            {"title": "A5 / Black", "price": "32.99", "compare_at_price": None, "sku": "LJ-A5-BLK", "inventory_quantity": 70, "option1": "A5", "option2": "Black"},
            {"title": "A6 / Brown", "price": "24.99", "compare_at_price": None, "sku": "LJ-A6-BRN", "inventory_quantity": 90, "option1": "A6", "option2": "Brown"},
        ],
        "options": [{"name": "Size"}, {"name": "Color"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&q=80"},
        ],
    },
    # ── One Out of Stock product for testing ──
    {
        "title": "Limited Edition Art Print",
        "body_html": "<p>Museum-quality giclée art print on archival matte paper. Each print is numbered and signed by the artist. Available in two sizes with optional framing.</p>",
        "vendor": "TrendArt",
        "product_type": "Home & Living",
        "tags": "home, art, print, limited edition, decor",
        "variants": [
            {"title": "12x16 / Unframed", "price": "45.00", "compare_at_price": None, "sku": "AP-12-UF", "inventory_quantity": 0, "option1": "12x16", "option2": "Unframed"},
            {"title": "18x24 / Unframed", "price": "65.00", "compare_at_price": None, "sku": "AP-18-UF", "inventory_quantity": 0, "option1": "18x24", "option2": "Unframed"},
            {"title": "18x24 / Framed", "price": "120.00", "compare_at_price": None, "sku": "AP-18-FR", "inventory_quantity": 0, "option1": "18x24", "option2": "Framed"},
        ],
        "options": [{"name": "Size"}, {"name": "Frame"}],
        "images": [
            {"src": "https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=800&q=80"},
        ],
    },
]

# ── Step 3: Collections ──────────────────────────────────────────────

COLLECTIONS = [
    {
        "title": "Electronics",
        "body_html": "Discover the latest in tech — headphones, smartwatches, speakers and more.",
        "image_src": "https://images.unsplash.com/photo-1468495244123-6c6c332eeece?w=1200&q=80",
        "product_types": ["Electronics"],
    },
    {
        "title": "Clothing & Fashion",
        "body_html": "Timeless style essentials for every season.",
        "image_src": "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1200&q=80",
        "product_types": ["Clothing"],
    },
    {
        "title": "Home & Living",
        "body_html": "Beautiful pieces to make your space feel like home.",
        "image_src": "https://images.unsplash.com/photo-1616046229478-9901c5536a45?w=1200&q=80",
        "product_types": ["Home & Living"],
    },
    {
        "title": "Beauty & Care",
        "body_html": "Premium skincare and beauty essentials for your daily routine.",
        "image_src": "https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=1200&q=80",
        "product_types": ["Beauty"],
    },
    {
        "title": "Bags & Accessories",
        "body_html": "Elevate your look with handcrafted bags and accessories.",
        "image_src": "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=1200&q=80",
        "product_types": ["Bags", "Stationery"],
    },
    {
        "title": "On Sale",
        "body_html": "Don't miss out — our best deals and discounted items.",
        "image_src": "https://images.unsplash.com/photo-1607082349566-187342175e2f?w=1200&q=80",
        "product_types": [],  # Will manually add products with compare_at_price
    },
]


def create_product(product_data):
    """Create a product with variants and images."""
    payload = {
        "product": {
            "title": product_data["title"],
            "body_html": product_data["body_html"],
            "vendor": product_data["vendor"],
            "product_type": product_data["product_type"],
            "tags": product_data["tags"],
            "status": "active",
            "options": product_data["options"],
            "variants": [],
            "images": [{"src": img["src"]} for img in product_data["images"]],
        }
    }

    for v in product_data["variants"]:
        variant = {
            "title": v["title"],
            "price": v["price"],
            "sku": v["sku"],
            "inventory_management": "shopify",
            "option1": v.get("option1"),
            "option2": v.get("option2"),
            "option3": v.get("option3"),
        }
        if v.get("compare_at_price"):
            variant["compare_at_price"] = v["compare_at_price"]
        payload["product"]["variants"].append(variant)

    result = api_post("products.json", payload)
    product = result["product"]

    # Set inventory quantities
    for i, v in enumerate(product["variants"]):
        if i < len(product_data["variants"]):
            target_qty = product_data["variants"][i].get("inventory_quantity", 0)
            inv_item_id = v["inventory_item_id"]
            # Get location
            locations = api_get("locations.json")
            location_id = locations["locations"][0]["id"]
            # Set inventory level
            try:
                requests.post(
                    f"{BASE_URL}/inventory_levels/set.json",
                    headers=HEADERS,
                    json={
                        "location_id": location_id,
                        "inventory_item_id": inv_item_id,
                        "available": target_qty,
                    }
                )
            except Exception:
                pass

    return product


def create_collection(coll_data, product_ids):
    """Create a custom collection and add products."""
    payload = {
        "custom_collection": {
            "title": coll_data["title"],
            "body_html": coll_data["body_html"],
            "image": {"src": coll_data["image_src"]},
            "published": True,
        }
    }
    result = api_post("custom_collections.json", payload)
    collection = result["custom_collection"]
    coll_id = collection["id"]

    # Add products to collection via collects
    for pid in product_ids:
        try:
            api_post("collects.json", {
                "collect": {
                    "product_id": pid,
                    "collection_id": coll_id,
                }
            })
        except Exception as e:
            print(f"  Warning adding product {pid} to collection: {e}")
        time.sleep(0.2)

    return collection


def main():
    print("=" * 60)
    print("  Shopify Store Population Script")
    print("=" * 60)
    print()

    # Step 1: Clean up
    delete_all_products()
    delete_custom_collections()
    delete_smart_collections()

    # Step 2: Create products
    print("=== Creating products ===")
    created_products = {}  # product_type -> list of (product_id, has_compare_at)

    for i, p in enumerate(PRODUCTS):
        print(f"  [{i+1}/{len(PRODUCTS)}] Creating: {p['title']}...")
        product = create_product(p)
        ptype = p["product_type"]
        has_compare = any(v.get("compare_at_price") for v in p["variants"])

        if ptype not in created_products:
            created_products[ptype] = []
        created_products[ptype].append({
            "id": product["id"],
            "title": product["title"],
            "has_compare_at": has_compare,
        })
        time.sleep(0.5)  # Rate limiting

    print(f"\n  Created {len(PRODUCTS)} products total.\n")

    # Step 3: Create collections
    print("=== Creating collections ===")
    for coll in COLLECTIONS:
        product_ids = []

        if coll["title"] == "On Sale":
            # Add all products that have compare_at_price
            for ptype_list in created_products.values():
                for p in ptype_list:
                    if p["has_compare_at"]:
                        product_ids.append(p["id"])
        else:
            for ptype in coll["product_types"]:
                for p in created_products.get(ptype, []):
                    product_ids.append(p["id"])

        print(f"  Creating collection: {coll['title']} ({len(product_ids)} products)...")
        create_collection(coll, product_ids)
        time.sleep(0.5)

    print()
    print("=" * 60)
    print("  DONE! Store populated successfully.")
    print(f"  Products: {len(PRODUCTS)}")
    print(f"  Collections: {len(COLLECTIONS)}")
    print("=" * 60)


if __name__ == "__main__":
    main()
