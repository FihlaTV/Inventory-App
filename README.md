# Inventory App
Android app which lets the user make a sqlite databse of items in any inventory. User can add the name, quantity, price and supplier's phone along with an option to link an image to the product from the gallery.

# Catalog Activity
The main activity contains the list of all the items stored in the database table : "items"

The main acitvity contains a "SOLD" button along with each item, clicking on which reduces the quantity of the product by one. Logic has been added so that the quantity does not go into negative integers.

# Editor Activity
Upon clicking on any item in the CatalogActivity, user is taken to the EditorActivity wherein all the other information about the item is displayed and is available for the user to edit. 
"Select image" button opens the gallery intent and lets the user choose a photo of the product.
The "+" and "-" buttons increase and decrease the quantity of the product respectively.
Buttons for calling the supplier, saving and deleting the item have been added.

Instead of an SQLiteOpenHelper class, use of CursorLoaders has been everywhere to keep the main UI thread free from database operations.


# Minimum API level : 15

# Test devices:
  1. Nexus 5X (Android 8.0) - Emulator
  2. Samsung Galaxy S7 Edge (Android 7.0) - Physical Device.
