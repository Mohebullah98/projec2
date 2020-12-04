import java.io.*;
import java.util.*;
import java.lang.Math;
import java.io.IOException;
class Main {
  public static int numRows;
  public static int numCols;
  public static int minVal;
  public static int maxVal;
  public static int maxHight;
  public static int offset;
  public static int thrVal;
  public static int  histAry[];
  public static int  histGraph[][];
  public static int  GaussAry[];
  public static int GaussGraph[][];
  public static int gapGraph[][];
  public static int bestThrVal;
 
 
  
  
  
  public static void loadHist(int histArray[], Scanner scan){
    
    while(scan.hasNextInt()){
      int pixel = scan.nextInt();
      int pixel_val = scan.nextInt();
      histArray[pixel]= pixel_val;
    }
    
    }
  public static int maxheight(int histArray[]){
    int max =0;
    for(int i=0;i<maxVal+1;i++){
       if (max<histArray[i]) max = histArray[i];
    }
    return max;
  }
  public static void plotHistGraph(int  histGraph[][]){
    for(int i=0;i<maxVal+1;i++){
      for(int j=0;j<maxHight+1;j++){
        histGraph[i][histAry[i]] = 1;
      }
    }
  }
  public static double computeMean(int leftIndex, int rightIndex, int maxheight){
    maxheight=0;
    int sum=0;
    int numPixels=0;
    int index = leftIndex;
    while(index<rightIndex){
    sum+=(histAry[index]*index);
    numPixels+=histAry[index];
    if (histAry[index]>maxheight){
      maxheight=histAry[index];
    }
    index++;
    }
    return (double) sum/ (double)numPixels;
    }
 public static double computeVar(int leftIndex, int rightIndex, double mean){
    double sum = 0.0;
    int numPixels =0;
    int index = leftIndex;
    while (index<rightIndex){
    sum+= (double)histAry[index] * ((double)(index-mean)* (double)(index-mean));
    numPixels+= histAry[index];
    
    index++;
    }
    return (double) sum/(double)numPixels;
  }
  public static double modifiedGauss(int x, double mean, double var, int maxheight){
    return (double) (maxheight * Math.exp( -(((x-mean) * (x-mean)) / (2*var))));
  }
  public static void set1dZero(int ary1D[]){
    for (int i=0;i<ary1D.length;i++){
      ary1D[i]=0;
    }
  }
  public static void set2dZero(int ary2D[][]){
    for(int i=0;i<ary2D.length;i++){
      for(int j=0;j<ary2D[i].length;j++){
        ary2D[i][j]=0;
      }
    }
  }

  public static double fitGauss(int leftIndex, int rightIndex, int GaussAry[], int GaussGraph[][]){
    double mean;
    double var;
    double sum;
    double Gval;
    double maxGval;
    sum=0.0;
    mean = computeMean(leftIndex, rightIndex, maxHight);
    var = computeVar(leftIndex, rightIndex, mean);
    int index = leftIndex;
    while(index<=rightIndex){
    Gval = modifiedGauss(index, mean, var, maxHight);
    sum+= Math.abs(Gval - (double) histAry[index]);
    GaussAry[index]= (int) Gval;
    GaussGraph[index][(int) Gval]=1;
    index++;
    }
    return sum;
  }
  public static void bestThrPlot(int bestThrVal){
    double sum1;
    double sum2;
    set1dZero(GaussAry);
    set2dZero(GaussGraph);
    set2dZero(gapGraph);
    sum1 = fitGauss(0, bestThrVal, GaussAry, GaussGraph);
    sum2 = fitGauss(bestThrVal, maxVal, GaussAry, GaussGraph);
    plotGaps(histAry, GaussGraph, gapGraph);
  }

  public static void plotGaps(int histAry[], int GaussGraph[][],int gapGraph[][]){
    int index = minVal;
    while(index<maxVal){
      int first = Math.min(histAry[index], GaussAry[index]);
      int last = Math.max(histAry[index], GaussAry[index]);
      while(first<last){
        gapGraph[index][first]=1;
        first++;
      }
      index++;

    }
  }
  public static int biMeanGauss(int thrVal, File outFile2, File outFile3) throws IOException{
    double sum1;
    double sum2;
    double total;
    double minSumDiff;
    int bestThr = thrVal;
    minSumDiff=999999.0;
    while(thrVal< (maxVal-offset)){
    set1dZero(GaussAry);
    set2dZero(GaussGraph);
    set2dZero(gapGraph);
    sum1 = fitGauss(0, thrVal, GaussAry, GaussGraph);
    sum2 = fitGauss(thrVal, maxVal, GaussAry, GaussGraph);
    total = sum1 + sum2;
    if ( total< minSumDiff){
      minSumDiff = total;
      bestThr = thrVal;
    }
    thrVal++;
    prettyPrint(GaussGraph, outFile2);
    plotGaps(histAry, GaussGraph, gapGraph);
    prettyPrint(gapGraph, outFile3);
    }
    return bestThr;
  }
  
    
  public static void prettyPrint(int graph[][],File outFile) throws IOException{
    FileWriter writer = new FileWriter(outFile,true);
    if(graph == histGraph) writer.write("2-D Display of Histogram\n");
    if(graph==GaussGraph && bestThrVal !=0) writer.write("Best Threshold Value: " + bestThrVal + "\n 2-D Display of two Gaussian Curves overlying the histogram\n");
    else if(graph ==GaussGraph) writer.write("2-D Display of two Gaussian Curves overlying the histogram\n");
    if(graph == gapGraph ) writer.write("2-D Display of the gaps between two best Gaussian curves and histogram\n") ;
    for(int i=0;i<graph.length;i++){
      for(int j=0;j<graph[i].length;j++){
        if(graph[i][j]==0){
         writer.write(" ");          
        }
        else{ writer.write("*");
        }
        
      }
      writer.write("\n");
    }
    
    writer.close();
    
  }
  public static void main(String[] args) throws IOException {
   File inFile = new File(args[0]);
   Scanner scan = null;
   scan = new Scanner(inFile);
   
   File outFile1 = new File(args[1]);
   File outFile2 = new File(args[2]); 
   File outFile3 = new File(args[3]); 
   
   outFile1.createNewFile();
   outFile2.createNewFile();
   outFile3.createNewFile();
   
    numRows = scan.nextInt();
    numCols = scan.nextInt();
    minVal = scan.nextInt();
    maxVal = scan.nextInt();
    
   
   offset = (maxVal - minVal)/10;
   thrVal = offset;
   histAry = new int[maxVal+1];
   loadHist(histAry, scan);
   maxHight = maxheight(histAry);
   GaussAry = new int[maxVal+1];
   histGraph = new int[maxVal+1][maxHight+1];
   GaussGraph = new int[maxVal+1][maxHight+1];
   gapGraph = new int[maxVal+1][maxHight+1];

   
   plotHistGraph(histGraph);
   prettyPrint(histGraph, outFile1);
   
  bestThrVal = biMeanGauss(thrVal,outFile2,outFile3);
   
   bestThrPlot(bestThrVal);
   prettyPrint(GaussGraph, outFile1);
    
   prettyPrint(gapGraph, outFile1);

   scan.close();
   
   
  
   }
  
   }
  
  

