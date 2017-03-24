package tools.vitruv.framework.tests.change.rootobject

import org.junit.Test

class ChangeDescription2InsertRootEObjectTest extends ChangeDescription2RootChangeTest {
	
	@Test
	def void insertCreateRootEObjectInResource(){
		// prepare
		startRecordingOnResourceSet
		// test
		insertRootEObjectInResource(this.resource)
		// assert
		val isCreate = true
		assertInsertRoot(0, isCreate, this.uri, this.resource)
		//claimChange(1).assertReplaceSingleValueEAttribute(null, this.rootElement.id)
	}
	
	@Test
	def void insertCreateTwoRootEObjectsInResource(){
		// prepare
		startRecordingOnResourceSet
		// test
		insertRootEObjectInResource(this.resource)
		// assert
		val isCreate = true
		assertInsertRoot(0, isCreate, this.uri, this.resource)
		
		startRecordingOnResourceSet
		// test
		insertRootEObjectInResource2(this.resource)
		// assert
		assertInsertRoot2(0, isCreate, this.uri, this.resource)
		
		//claimChange(1).assertReplaceSingleValueEAttribute(null, this.rootElement.id)
	}
}

