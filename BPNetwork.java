package BPN.Net;

import com.ibm.dthreads.*;
import org.joone.engine.*;
import org.joone.net.*;
import org.joone.engine.learning.*;
import org.joone.io.*;
import org.joone.engine.weights.*;


import java.util.*;
import java.io.*;



  
public class Network extends DThread implements NeuralNetListener {
    
    int NUM_HIDDEN;
    final int NUM_INPUTS = 60;
    final int NUM_OUTPUTS = 2;
    
    final int NO_PATTERNS = 104;
    int id;
    final int NO_ITERATIONS = 5000;
    final double ETA = 0.3;
    
    final double ALPHA = 0.1;
    
     LinearLayer input;
     SigmoidLayer hidden ;
     SigmoidLayer output;
     TeachingSynapse trainer;
        FullSynapse synapse_IH ;
        FullSynapse synapse_HO;
     Monitor monitor;

	GUI g;
       
    ////FILE NAMES/////////////
        String trainResultsFile = "train_results.txt";
	  String trainInputFile = "train_inputs.txt";
	  String trainOutputFile = "train_output.txt";
	  String trainErrorFile = "train_error.txt";

	  String testResultsFile = "test_results.txt";
	  String testInputFile = "test_inputs.txt";
	  String testOutputFile = "test_output.txt";
	  String testErrorFile = "test_error.txt";

    
    
    /** Creates new BPN*/
    public Network(int NUM_HIDDEN) {
              this.NUM_HIDDEN = 12;
        
    }
    
    private void SetUpNetwork()
    {
        g = new GUI("Neural Network", id);

        input = new LinearLayer();
        hidden = new SigmoidLayer();
       output = new SigmoidLayer();


        
        input.setLayerName("input");
        hidden.setLayerName("hidden");
             
        output.setLayerName("output");

       
	  input.setRows(NUM_INPUTS);       
        hidden.setRows(NUM_HIDDEN);
        output.setRows(NUM_OUTPUTS);
        
        
   
        synapse_IH = new FullSynapse(); /* input -> hidden conn. */
        synapse_HO = new FullSynapse(); /* hidden -> output conn. */
        
        synapse_IH.setName("IH");
        synapse_HO.setName("HO");
   
        input.addOutputSynapse(synapse_IH);
        hidden.addInputSynapse(synapse_IH);
      
        hidden.addOutputSynapse(synapse_HO);
        output.addInputSynapse(synapse_HO);
    }

    private void SetUpMonitor()
    {
        monitor = new Monitor();
    
      
        
        monitor.setLearningRate(ALPHA);
        monitor.setMomentum(ETA);
        /*
         * Passe the Monitor to all components
         */
        input.setMonitor(monitor);
        hidden.setMonitor(monitor);
        output.setMonitor(monitor);
        
        /* The application registers itself as monitor's listener
         * so it can receive the notifications of the net.
         */
        monitor.addNeuralNetListener(this);
        
    }
    private void TrainNet()
    {
         FileInputSynapse inputStream = new FileInputSynapse();
        inputStream.setFileName(trainInputFile);
        inputStream.setAdvancedColumnSelector("1-60");
        input.addInputSynapse(inputStream);
        
        
        trainer = new TeachingSynapse();
        trainer.setMonitor(monitor);
        
          
        /* Setting of the file containing the desired responses,
         provided by a FileInputSynapse */
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
        
  		    
        //set number of hidden units
        
     
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
    }
    private void GetSnapShot(String filename)
    {
         try {
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

		input.join();
		hidden.join();
		output.join();

   	 FileWriter fw = new FileWriter(new File(filename));
        int i;
        for (i = 0; i < NO_PATTERNS;  i++)
        {
            double pattern[] = results.getNextPattern();
            g.addText( " " + pattern[0] + " " + pattern[1]);
            fw.write( (pattern[0]>= 0.5 ? 1 : 0) + " " + (pattern[1]>= 0.5 ? 1 : 0) + "\n");           
            
        }
        
        fw.close();
	} catch( Exception e)
		{
		g.addText(e + "");
		}
    }
    public void Go() {
      
        try {
	
        //initialize the layers and connections    
        SetUpNetwork();
        
        //set up the network runner
        SetUpMonitor();
      
        //train the network using training files
        TrainNet();
        
        //get a file output of the net
        GetSnapShot(trainResultsFile);      
       
       //test the network with the test files
       	TestNetwork();
        
        }     catch (Exception ex) {
            g.addText(ex + "");
        }
      
    }
    
	public void TestNetwork()
	{
	   try {
    
     	  input.removeAllInputs();
        FileInputSynapse inputStream = new FileInputSynapse();
        /*  file that contains the test data */
        inputStream.setFileName(testInputFile);
        inputStream.setAdvancedColumnSelector("1-60");
        
        input.addInputSynapse(inputStream);
        
 	GetSnapShot(testResultsFile);
           }   
        catch (Exception ex) {
            g.addText(ex + "");
        }
        
}


    public void netStopped(NeuralNetEvent e) {
        g.addText("Training finished");
        
//        NeuralNet nnet = new NeuralNet();
//        nnet.addLayer(hidden);
//        nnet.setInputLayer(input);
//        nnet.addLayer(hidden);
//        nnet.setOutputLayer(output);
        
     
        
    }
    
    public void cicleTerminated(NeuralNetEvent e) {
       
    }
    
    public void netStarted(NeuralNetEvent e) {
        g.addText("Training...");
    }
    
    public void errorChanged(NeuralNetEvent e) {
        Monitor mon = (Monitor)e.getSource();
        long c = mon.getCurrentCicle();
        long cl = c / 50;
        /* We want print the results every 1000 cycles */
        if ((cl * 50) == c)
            g.addText(c + " cycles remaining - Error = " + mon.getGlobalError());
        
        
    }
    
    public void netStoppedError(NeuralNetEvent e,String error) {
        
    }
   
    public void run()
    {
        id = DThread.getContext().getIdentity();
          Go();
    }
      
     
}
