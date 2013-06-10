/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package computing.methods;

import computing.methodsinterfaces.*;
import ca.Area;
import static computing.methodsinterfaces.IterationMethodI.parameters;
import java.util.Random;

/**
 *
 * @author Andrewman
 */
//Klasa implementująca IterationMethodI będzie używana do obliczania kolejnego kroku iteracji
//Będzie wykorzystywana przez węzły sieci
public class FeForwardMethod3D implements IterationMethodI {

    
    private double tempX = 0.0;
    private double tempC = 0.0;
    private double tempY = 0.0;
    private double tempZ = 0.0;
    
    public FeForwardMethod3D() {
        //parameters.put(DiffusionParams.Random.name, new Random().nextDouble());
        // Dodane z Krzyska dyfuzji
        parameters.put("D0", (double)175000);
        parameters.put("hx", (double)1.0);
        parameters.put("hy", (double)1.0);
        parameters.put("hz", (double)1.0);
        parameters.put("R", (double)8.314);
        parameters.put("Q", (double)143320);
        parameters.put("Ti", (double)1000);
        parameters.put("K", (double)(parameters.get("Ti") + 273.15));
        parameters.put("D", (double)(parameters.get("D0") * (Math.exp(-((parameters.get("Q") / (parameters.get("R") * (parameters.get("K")))))))));
        parameters.put("g", (double)((1 * 1 * 1) / (6 * parameters.get("D"))) - (0.01 * ((1 * 1 * 1) / (6 * parameters.get("D")))));
    }
    
    // z dyfuzji Krzyska
    @Override
    public void nextIteration(Area area) { 
        for (int i = 1; i < area.getRealWidth() - 2; i++) {
            for (int j = 1; j < area.getRealHeight() - 2; j++) {
                for (int k = 1; k < area.getRealDepht() - 2; k++) {
                    // i odwrotnie z j ?
                    tempC = ((((-1) * (2 / Math.pow(1, 2))) + ((-1) * (2 / Math.pow(1, 2))) + ((-1) * (2 / Math.pow(1, 2))) + (1 /(parameters.get("D") * parameters.get("g")))) * area.getValue(i, j, k));
                    //tempC = ((((-1) * (2 / Math.pow(hx, 2))) + ((-1) * (2 / Math.pow(hy, 2))) + ((-1) * (2 / Math.pow(hz, 2))) + (1 / (D * g))) * temp.getC(j, i, k));
                    tempX = (area.getValue(i+1,j, k)  + (area.getValue(i-1,j, k)))/(Math.pow(1, 2));
                    tempY = (area.getValue(i,j+1, k)  + (area.getValue(i,j-1, k)))/(Math.pow(1, 2));
                    tempZ = (area.getValue(i,j, k+1)  + (area.getValue(i,j, k-1)))/(Math.pow(1, 2));
                    area.setValue(i,j, k, ( parameters.get("D") * parameters.get("g") * (tempC + tempX + tempY + tempZ)));
                }
            }
        }
    }
    
    // Stara nextInteration
    /*
    @Override
    public void nextIteration(Area area) {
        for (int i = 1; i < area.getRealWidth() - 2; i++) {
            for (int j = 1; j < area.getRealHeight() - 2; j++) {
                for (int k = 1; k < area.getRealDepht() - 2; k++) {

                    double newValue = (area.getValue(i, j - 1, k)
                            + area.getValue(i - 1, j, k) + area.getValue(i, j, k) + area.getValue(i + 1, j, k)
                            + area.getValue(i, j + 1, k) + area.getValue(i, j, k + 1) + area.getValue(i, j, k - 1)) / 7;
                    area.setValue(i, j, k, newValue + parameters.get(DiffusionParams.Random.name));
                }
            }
        }
    }    
    */
    
    enum DiffusionParams{
        
        Random("Random");    
        String name;
        
        DiffusionParams(String name) {
            this.name = name;
        }
    }
}
