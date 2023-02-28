import java.io.*;
import java.util.ArrayList;

public class SingleThreadedComp {

    private int offset = 0;

    public static void main(String[] args) {
        //Handle command line arguments
        if (args.length != 2) {
            System.out.println("Usage: java SingleThreadedComp <input file> <kernel file>");
            System.exit(1);
        }

        // Create instance of class
        SingleThreadedComp stc = new SingleThreadedComp();

        // Get input array and kernel array
        int[][] inputArr = null;
        int[][] kernelArr = null;
        try {
            inputArr = stc.getArr(args[0]);
            kernelArr = stc.getArr(args[1]);
        } catch (IOException e) {
            System.out.println("Error reading file");
            System.exit(1);
        }

        // Pad input array
        int[][] paddedArr = stc.padArray(inputArr);
        
        // Convolve
        int[][] convolvedArr = stc.convolve(paddedArr, kernelArr);

        // Print convolved array
        stc.printArr(convolvedArr);
    
    }

    public int[][] getArr(String FileName) throws IOException {
        // Get main array
        BufferedReader br = new BufferedReader(new FileReader(FileName));

        // Array list of int arrays
        ArrayList<int[]> arrList = new ArrayList<int[]>();

        String line = br.readLine();
        while (line != null) {
            String[] lineArr = line.split(" ");
            int[] intArr = new int[lineArr.length];
            for (int i = 0; i < lineArr.length; i++) {
                intArr[i] = Integer.parseInt(lineArr[i]);
            }
            arrList.add(intArr);
            line = br.readLine();
        }

        // Convert array list to 2D array
        int[][] mainArr = new int[arrList.size()][];
        for (int i = 0; i < arrList.size(); i++) {
            mainArr[i] = arrList.get(i);
        }

        br.close();

        return mainArr;
    }

    public void printArr(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int[][] padArray(int[][] arr) {
        // determine the size of the kernel, assuming it is square
        // Then half the size to get the offset
        int kernelSize = arr.length / 2;

        // This gets added to the index to get the correct index in the padded array
        this.offset = kernelSize + 1;

        // Create padded array
        int[][] paddedArr = new int[arr.length + (offset * 2)][arr[0].length + (offset * 2)];

        // Copy array
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                paddedArr[i + offset][j + offset] = arr[i][j];
            }
        }

        // Print padded array
        // printArr(paddedArr);

        return paddedArr;
    }

    public int[][] convolve(int[][] arr, int[][] kernel) {
        // Create convolved array
        int[][] convolvedArr = new int[arr.length - (offset * 2)][arr[0].length - (offset * 2)];

        // Convolve
        for (int i = offset; i < arr.length - offset; i++) {
            for (int j = offset; j < arr[i].length - offset; j++) {
                int sum = 0;
                for (int k = 0; k < kernel.length; k++) {
                    for (int l = 0; l < kernel[k].length; l++) {
                        sum += arr[i - offset + k][j - offset + l] * kernel[k][l];
                    }
                }
                convolvedArr[i - offset][j - offset] = sum;
            }
        }

        return convolvedArr;
    }
}
