package model.user.utility;

public class LogUtility {
    /**
     * Metoda obliczająca BMI
     * @param weight
     * @param height
     * @return
     */
    public static Double calculateBMI(float weight, int height){
        return (double)weight / (height*height) * 10000;
    }
    /**
     * Metoda obliczająca BMR
     * @param isFemale
     * @param weight
     * @param height
     * @param age
     * @return
     */
    public static Integer calculateBMR(boolean isFemale, float weight, int height, int age){
        int plus;
        if(isFemale)
            plus = -161;
        else plus = 5;
        return (int)(9.99 * weight + 6.25 * height - 4.92 * age + plus);
    }
    /**
     * Metoda obliczająca wskaznik Broca
     * @param isFemale
     * @param height
     * @return
     */
    public static Double calculateBroc(boolean isFemale, int height){
        double x;
        if(isFemale) x = 0.85;
        else x = 0.9;
        return (height - 100) * x;
    }
    /**
     * Metoda obliczająca ilosc tluszczu
     * @param isFemale
     * @param weight
     * @param waist
     * @return
     */
    public static Double calculateFat(boolean isFemale, float weight, float waist){
        double minus;
        if(isFemale) minus = 76.76;
        else minus = 98.42;
        return ((4.15 * waist) / 2.54-0.082 * weight * 2.2-minus)/(weight * 2.2)*100;
    }
    /**
     * Metoda obliczająca WHR
     * @param hips
     * @param waist
     * @return
     */
    public static Double calculateWHR(float hips, float waist){
        return (double)waist/hips;
    }
}
