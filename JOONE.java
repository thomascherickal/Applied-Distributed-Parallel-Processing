package ANNs.Sonar.BPN;


package ANNs.Sonar.BPN;

import org.joone.engine.*;
import org.joone.net.*;
import org.joone.engine.learning.*;
import org.joone.io.*;
import org.joone.engine.weights.*;


import java.util.*;
import java.io.*;




public class Network implements NeuralNetListener {

    final int NUM_HIDDEN = 24;
    final int NUM_INPUTS = 60;
    final int NUM_OUTPUTS = 2;

    final int NO_PATTERNS = 104;

    final int NO_ITERATIONS = 10;
    final double ETA = 0.5;

    final double ALPHA = 0.1;

     LinearLayer input;
     SigmoidLayer hidden ;
     SigmoidLayer output;
     TeachingSynapse trainer;
        FullSynapse synapse_IH ;
        FullSynapse synapse_HO;
     Monitor monitor;

	////FILE NAMES/////////////
        String trainResultsFile = "train_results.txt";
	  String trainInputFile = "train_inputs.txt";
	  String trainOutputFile = "train_output.txt";
	  String trainErrorFile = "train_error.txt";

	  String testResultsFile = "test_results.txt";
	  String testInputFile = "test_inputs.txt";
	  String testOutputFile = "test_outputs.txt";
	  String testErrorFile = "test_error.txt";



        RpropLearner rprop;

    /** Creates new BPN*/
    public Network() {



            Go();

    }



    public void Go() {
        /*
         * First, creates the three Layers
         */


        input = new LinearLayer();
        hidden = new SigmoidLayer();
       output = new SigmoidLayer();

       rprop = new RpropLearner();

        input.setLayerName("input");
        hidden.setLayerName("hidden");

        output.setLayerName("output");

        /* sets their dimensions */
        input.setRows(NUM_INPUTS);
        hidden.setRows(NUM_HIDDEN);
        output.setRows(NUM_OUTPUTS);



        /*
         * Now create the two Synapses
         */
        synapse_IH = new FullSynapse(); /* input -> hidden conn. */
        synapse_HO = new FullSynapse(); /* hidden -> output conn. */

        synapse_IH.setName("IH");
        synapse_HO.setName("HO");
        /*
         * Connect the input layer with the hidden layer
         */
        input.addOutputSynapse(synapse_IH);
        hidden.addInputSynapse(synapse_IH);
        /*
         * Connect the hidden layer withthe output layer
         */
        hidden.addOutputSynapse(synapse_HO);
        output.addInputSynapse(synapse_HO);

        /*
         * Create the Monitor object and set the learning parameters
         */
        monitor = new Monitor();

        monitor.getLearners().add(0, "org.joone.engine.BasicLearner");
        monitor.getLearners().add(1, "org.joone.engine.BasicLearner");
        monitor.getLearners().add(2, "org.joone.engine.RpropLearner");

        monitor.setLearningRate(ALPHA);
        monitor.setMomentum(ETA);

        input.setMonitor(monitor);
        hidden.setMonitor(monitor);
        output.setMonitor(monitor);

        monitor.addNeuralNetListener(this);

        FileInputSynapse inputStream = new FileInputSynapse();
        /* This is the file that contains the input data */
        inputStream.setFileName(trainInputFile);
        inputStream.setAdvancedColumnSelector("1-60");
        input.addInputSynapse(inputStream);


        trainer = new TeachingSynapse();
        trainer.setMonitor(monitor);


        FileInputSynapse samples = new FileInputSynapse();
        samples.setAdvancedColumnSelector("1, 2");
        samples.setFileName(trainOutputFile);


        trainer.setDesired(samples);

        /* Creates the error output file */
        FileOutputSynapse error = new FileOutputSynapse();
        error.setFileName(trainErrorFile);
        //error.setBuffered(false);
        trainer.addResultSynapse(error);

        /* Connects the Teacher to the last layer of the net */
        output.addOutputSynapse(trainer);













        /* All the layers must be activated invoking their method start;
         * the layers are implemented as Runnable objects, then they are
         * instanziated on separated threads.
         */
        input.start();
        hidden.start();
        output.start();

        monitor.setTrainingPatterns(NO_PATTERNS); /* # of rows (patterns) contained in the input file */
        monitor.setTotCicles(NO_ITERATIONS); /* How many times the net must be trained on the input patterns */
        monitor.setLearning(true); /* The net must be trained */
        monitor.Go(); /* The net starts the training job */

        input.join();
        hidden.join();
        output.join();

        output.removeAllOutputs();

          MemoryOutputSynapse results = new MemoryOutputSynapse();

        output.addOutputSynapse(results);

        monitor.setLearning(false);
        monitor.setTrainingPatterns(NO_PATTERNS);
        monitor.setTotCicles(1);
        input.start();
        hidden.start();
        output.start();
        monitor.Go();

        try {
        FileWriter fw = new FileWriter(new File(trainResultsFile));
        int i;
        for (i = 0; i < NO_PATTERNS;  i++)
        {
            double pattern[] = results.getNextPattern();
            System.out.println( " " + pattern[0] + " " + pattern[1]);
            fw.write( (pattern[0]>= 0.5 ? 1 : 0) + " " + (pattern[0]> 0.5 ? 1 : 0) + "\n");

        }

        fw.close();
        }


        catch (Exception ex) {
            System.out.println(ex);
        }

    }

    public void netStopped(NeuralNetEvent e) {
        System.out.println("Training finished");

        System.exit(0);
    }

    public void cicleTerminated(NeuralNetEvent e) {

    }

    public void netStarted(NeuralNetEvent e) {
        System.out.println("Training...");
    }

    public void errorChanged(NeuralNetEvent e) {
        Monitor mon = (Monitor)e.getSource();
        long c = mon.getCurrentCicle();
        long cl = c / 50;
        /* We want print the results every 1000 cycles */
        if ((cl * 50) == c)
            System.out.println(c + " cycles remaining - Error = " + mon.getGlobalError());


    }

    public void netStoppedError(NeuralNetEvent e,String error) {

    }
    public static void main(String args[])
    {
        new Network();
    }

}
