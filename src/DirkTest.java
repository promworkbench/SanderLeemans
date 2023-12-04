import java.io.File;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot2Image;
import org.processmining.plugins.graphviz.dot.Dot2Image.Type;
import org.processmining.plugins.inductiveVisualMiner.plugins.GraphvizPetriNet;

public class DirkTest {
	public static void main(String... args) {
		EfficientTree tree = InlineTree
				.concurrent(//
						InlineTree.xor( //
								InlineTree.tau(), // 
								InlineTree.leaf("01_HOOFD_065_2")), //
						InlineTree.sequence(//
								InlineTree.interleaved( //
										InlineTree.xor(//
												InlineTree.tau(), //
												InlineTree.sequence(//
														InlineTree.leaf("03_DRZ_010"), // 
														InlineTree.leaf("04_BPT_005"))),
										InlineTree.xor(//
												InlineTree.tau(), //
												InlineTree.sequence(//
														InlineTree.leaf("01_HOOFD_065_0"),
														InlineTree.xor(//
																InlineTree.tau(), //
																InlineTree.leaf("01_HOOFD_090"))))), //
								InlineTree.xor(//
										InlineTree.tau(), //
										InlineTree.leaf("01_HOOFD_065_1"))), //
						InlineTree.xor(//
								InlineTree.tau(), //
								InlineTree.leaf("01_HOOFD_101")));

		AcceptingPetriNet net = EfficientTree2AcceptingPetriNet.convert(tree);

		Dot dot = GraphvizPetriNet.convert(net);

		Dot2Image.dot2image(dot, new File("C:\\Users\\sander\\Documents\\dot.png"), Type.png);

		System.out.println(net);
	}
}
