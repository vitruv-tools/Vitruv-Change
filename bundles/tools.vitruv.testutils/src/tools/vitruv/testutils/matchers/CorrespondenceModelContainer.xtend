package tools.vitruv.testutils.matchers

import tools.vitruv.change.correspondence.CorrespondenceModelView
import tools.vitruv.change.correspondence.Correspondence

interface CorrespondenceModelContainer {
	def CorrespondenceModelView<? extends Correspondence> getCorrespondenceModel()
}
