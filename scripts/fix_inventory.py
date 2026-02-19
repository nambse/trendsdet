#!/usr/bin/env python3
"""
Fix inventory levels for all products in the Shopify store.
The original populate script silently failed to set inventory quantities.
"""

import requests
import time
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


def get_location_id():
    locations = api_get("locations.json")
    loc = locations["locations"][0]
    print(f"Location: {loc['name']} (ID: {loc['id']})")
    return loc["id"]


def get_all_products():
    """Get all products with their variants."""
    all_products = []
    params = {"limit": 250, "fields": "id,title,variants"}
    while True:
        data = api_get("products.json", params)
        products = data.get("products", [])
        if not products:
            break
        all_products.extend(products)
        # Check for pagination
        link = None  # simplified, just get first page for our small store
        break
    return all_products


def set_inventory(inventory_item_id, location_id, available):
    """Set inventory level for a variant."""
    url = f"{BASE_URL}/inventory_levels/set.json"
    payload = {
        "location_id": location_id,
        "inventory_item_id": inventory_item_id,
        "available": available,
    }
    r = requests.post(url, headers=HEADERS, json=payload)
    if r.status_code >= 400:
        print(f"    ERROR {r.status_code}: {r.text[:200]}")
        return False
    return True


def main():
    print("=" * 60)
    print("  Shopify Inventory Fix Script")
    print("=" * 60)
    print()

    location_id = get_location_id()
    print()

    products = get_all_products()
    print(f"Found {len(products)} products\n")

    # Default quantity for products (50 for each variant)
    DEFAULT_QTY = 50

    # Products that should be out of stock (for testing)
    OUT_OF_STOCK_TITLES = ["Limited Edition Art Print"]

    total_variants = 0
    fixed_variants = 0

    for product in products:
        title = product["title"]
        is_oos = title in OUT_OF_STOCK_TITLES
        target_qty = 0 if is_oos else DEFAULT_QTY

        print(f"Product: {title} (target qty: {target_qty})")

        for variant in product["variants"]:
            inv_item_id = variant["inventory_item_id"]
            variant_title = variant["title"]
            current_qty = variant.get("inventory_quantity", 0)
            total_variants += 1

            print(f"  Variant: {variant_title} | Current: {current_qty} | Setting to: {target_qty}")

            if set_inventory(inv_item_id, location_id, target_qty):
                fixed_variants += 1
            else:
                print(f"    FAILED to set inventory!")

            time.sleep(0.3)  # Rate limiting

        print()

    print("=" * 60)
    print(f"  Done! Fixed {fixed_variants}/{total_variants} variants")
    print("=" * 60)


if __name__ == "__main__":
    main()
