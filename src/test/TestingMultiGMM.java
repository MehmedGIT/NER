/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import edu.stanford.nlp.classify.LinearClassifier;
import gmm.GMMD1Diag;
import gmm.GMMDiag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import linearclassifier.AnalyzeLClassifier;
import static linearclassifier.AnalyzeLClassifier.TESTFILE;
import linearclassifier.Margin;
import org.apache.commons.math3.distribution.MixtureMultivariateNormalDistribution;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.fitting.MultivariateNormalMixtureExpectationMaximization;
import org.apache.commons.math3.util.Pair;
import tools.CNConstants;
import tools.Histoplot;

/**
 *
 * @author synalp
 */
public class TestingMultiGMM {

        public TestingMultiGMM(){
            AnalyzeLClassifier analyzing = new AnalyzeLClassifier();
            //final float[] priors = computePriors(sclassifier,model);
            List<List<Integer>> featsperInst = new ArrayList<>(); 
            List<Integer> labelperInst = new ArrayList<>();        
            final float[] priors = {0.2f,0.3f,0.4f,0.1f};
            String sclass=CNConstants.ALL;

            analyzing.trainAllLinearClassifier(sclass,true,false,false);
            
            LinearClassifier model = analyzing.getModel(sclass);
            //analyzing.testingClassifier(true, sclass, false,false);
            analyzing.getValues(TESTFILE.replace("%S", sclass),model,featsperInst,labelperInst);
            Margin margin = analyzing.getMargin(sclass);
            margin.setFeaturesPerInstance(featsperInst);
            margin.setLabelPerInstance(labelperInst);
            double[] scores= new double[featsperInst.size()];
            Arrays.fill(scores, 0.0);            
            Histoplot.showit(margin.getScoreForAllInstancesLabel0(featsperInst,scores), featsperInst.size());
            
            
            int numinst=labelperInst.size();
            //Margin margin = new Margin();
            /*
            numinst=10;
            margin.setNumberOfInstances(numinst);
            
            Margin.GENERATEDDATA=true;
            margin.generateRandomScore(numinst,priors);
            */
            System.out.println("******  MULTIDIMENSIONAL GMM ********");
            GMMDiag gmmMD = new GMMDiag(priors.length, priors);
            gmmMD.train(margin);
            String mean="mean=[";
            for(int i=0; i<  gmmMD.getDimension();i++){
                for(int j=0; j<  gmmMD.getDimension();j++){
                    mean+= gmmMD.getMean(i,j);
                    if(j<gmmMD.getDimension()-1)
                        mean+=" , ";
                } 
                if(i<gmmMD.getDimension()-1)
                    mean+=";\n";
            }    
            mean+="]";
            System.out.println(mean);
            String var=" var=[";
            for(int i=0; i<  gmmMD.getDimension();i++){
                for(int j=0; j<  gmmMD.getDimension();j++){
                    var+= gmmMD.getVar(i,j,j);
                    if(j<gmmMD.getDimension()-1)
                        var+=" , ";                    
                }  
                if(i<gmmMD.getDimension()-1)
                    var+=";\n";
            }    
            var+="]";
            System.out.println(var);            
            
            System.out.println("GMM trained");            
            /*
            System.out.println("x=[");
            for(int i=0;i<numinst;i++){
                System.out.println(margin.getGenScore(i, 0)+","+margin.getGenScore(i, 1)+";");
            }*/
            System.out.println("]");
            /*
            MultivariateNormalMixtureExpectationMaximization mvarGauss = new MultivariateNormalMixtureExpectationMaximization(margin.getGenScore());
            MixtureMultivariateNormalDistribution initialMixture= MultivariateNormalMixtureExpectationMaximization.estimate(margin.getGenScore(), priors.length);
            mvarGauss.fit(initialMixture);
            MixtureMultivariateNormalDistribution mixture= mvarGauss.getFittedModel();
            List<Pair<Double,MultivariateNormalDistribution>> gaussians= mixture.getComponents();
            for(Pair pair:gaussians){
                Double prob= (Double) pair.getFirst();
                MultivariateNormalDistribution distr= (MultivariateNormalDistribution) pair.getSecond();
                System.out.println("means: " + distr.getMeans());
                System.out.println(" covariance : "+ distr.getCovariances().toString());
                System.out.println("sdev :"+ distr.getStandardDeviations());
            }
            //*/
    }
    
    public static void main(String[] args){
        TestingMultiGMM test = new TestingMultiGMM();
    }
}