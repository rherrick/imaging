//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.ui;
import java.util.Vector;
import java.awt.Choice;

public class ChoiceWithObjects extends Choice {

 	 /** holds the objects displayed in this choice */
	 private Vector objects = new Vector();

	 /** Adds the specified item to the end of list.
	 @param item The item to add
	 */
	 public void add(Object item) {
		 objects.addElement(item);
		 super.add(item.toString());
	 }
	
	 public void addItem(Object item) {
		 this.add(item);
	 }
    
	 /** inserts the given object at the specified position
    
	 @param item the item to insert
	 @param i the index where to insert the object
	   */
	 public void insert(Object item, int i)
	 {
		 super.insert(item.toString(), i);
		 objects.insertElementAt(item, i);
	 }

	 /** Return vector of all items in list
	 @return vector of all items in object list, as a clone of the internal
		 object vector, so the result can be modified with no harm
	 */
	 public Vector getObjectItems() {
	 	return (Vector) objects.clone();
	 }

	 /** Returns the object at position index
	 @param index The index >= 0
	 @return Item at position index
	 */
	 public Object getObjectItem(int index) {
	 	return objects.elementAt(index);
	 }

	 /** return the selected object
	 @return The selected object or <tt>null</tt>
	 */
	 public Object getSelectedObject() {
		 int index = super.getSelectedIndex();
		 if (index == -1) {
			 return null;
		 } else {
			 return this.getObjectItem(index);
		 }
	 }

	 /** Returns a vector of all selected objects.
	 @return Vector of all selected objects
	 */
	 public Object[] getSelectedObjects()
	 {
		 int selectedIndex = super.getSelectedIndex();
		 Object[] result = new Object[1];
		 result[0] = objects.elementAt(selectedIndex);
		 return result;
	 }

	 /** Remove all items from list
	  */
	 public void removeAll() {
		 super.removeAll();
		 objects.removeAllElements();
	 }

	 /** Remove item with index from list
	 @param index
	 */
	 public void remove(int index) {
		 super.remove(index);
		 objects.removeElementAt(index);
	 }

    
	 /** selects the object specified as parameter. If more than one object
	 equal to the given item exists, the one with the smallest index
	 is selected. If no such object exists, the selection remains
	 unchanged.
    
	 @param item the object to select
	   */
	 public void select(Object item)
	 {
		 int index = objects.indexOf(item);
		 if (index >= 0)
			 super.select(index);
	 }

	 /** Remove item from list. 
	 @param item The item to delete from the list
	 @exception IllegalArgumentException
	 */
	 public void remove(Object item) {
		 int index;
		 index = objects.indexOf(item);
		 if (index == -1) {
			 throw new IllegalArgumentException();
		 } else {
			 this.remove(index);
		 }
	 }
}
