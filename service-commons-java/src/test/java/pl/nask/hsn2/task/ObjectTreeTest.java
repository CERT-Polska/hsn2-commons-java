/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.1.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.nask.hsn2.task;

import junit.framework.Assert;

import org.testng.annotations.Test;

public class ObjectTreeTest {

	@Test
	public void testCreateRootObject() {
		ObjectTreeNode objectTree = new ObjectTreeNode(0);		
		Assert.assertEquals(1, objectTree.contextHeight());
		Assert.assertEquals(1, objectTree.treeSize());
	}
	
	@Test(dependsOnMethods="testCreateRootObject")
	public void testCreateNewObject() {
		ObjectTreeNode objectTree = new ObjectTreeNode(0);		
		ObjectTreeNode newContext = objectTree.newObject();
		
		Assert.assertEquals(2, newContext.contextHeight());
	}
	
	@Test(dependsOnMethods="testCreateNewObject")
	public void testCloseContext() {
		ObjectTreeNode objectTree = new ObjectTreeNode(0);		
		ObjectTreeNode newObject = objectTree.newObject();
		newObject = newObject.getParent();
		
		Assert.assertEquals(1, objectTree.contextHeight());
	}
	
	@Test
	public void testCountObjectsInTheTree() {
		ObjectTreeNode root = new ObjectTreeNode(0);
		
		ObjectTreeNode object1 = root.newObject();
		Assert.assertEquals(1, object1.treeSize());
		Assert.assertEquals(2, root.treeSize());
		
		ObjectTreeNode object2 = object1.newObject();		
		Assert.assertEquals(3, root.treeSize());
		Assert.assertEquals(2, object1.treeSize());
		Assert.assertEquals(1, object2.treeSize());		
		
		root.newObject();
		Assert.assertEquals(4, root.treeSize());
		Assert.assertEquals(2, object1.treeSize());
		Assert.assertEquals(1, object2.treeSize());
		
		object1.newObject();
		Assert.assertEquals(5, root.treeSize());
		Assert.assertEquals(3, object1.treeSize());
		Assert.assertEquals(1, object2.treeSize());
	}
}
