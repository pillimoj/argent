ALTER TABLE wishlist_access DROP COLUMN description;
ALTER TABLE wishlist_items ADD COLUMN description TEXT NOT NULL;