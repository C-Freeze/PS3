import java.io.*;
import java.util.ArrayList;

public class MultiThreadedComp {

    public static void main(String[] args) {
        // Handle command line arguments
        if (args.length != 3) {
            System.out.println("Usage: java SingleThreadedComp <input file> <kernel file> <number of threads>");
            System.exit(1);
        }

        // Create instance of class
        MultiThreadedComp stc = new MultiThreadedComp();

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

        // Create convolved array
        int[][] convolvedArr = new int[inputArr.length][inputArr[0].length];

        // Build thread array
        int numThreads = Integer.parseInt(args[2]);
        Thread[] threads = new Thread[numThreads];

        // Break up work
        int rowsPerThread = inputArr.length / numThreads;
        int startRow = 0;
        int endRow = rowsPerThread;

        //If the number of rows is not divisible by the number of threads
        //then the last thread will have to do more work
        if (inputArr.length % numThreads != 0) {
            endRow += inputArr.length % numThreads;
        }

        // Create and start threads
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new ConvolveThread(paddedArr, kernelArr, convolvedArr, startRow, endRow));
            threads[i].start();
            startRow = endRow;
            endRow += rowsPerThread;
        }

        // Wait for threads to finish
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println("Error joining threads");
                System.exit(1);
            }
        }

        // Print convolved array
        stc.printArr(convolvedArr);
    }

    public int offset;

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

        return paddedArr;
    }

    static class ConvolveThread implements Runnable {
        private int[][] paddedArr;
        private int[][] kernelArr;
        private int[][] convolvedArr; // reference to the convolved array
        private int startRow;
        private int endRow;

        public ConvolveThread(int[][] paddedArr, int[][] kernelArr, int[][] convolvedArr, int startRow, int endRow) {
            this.paddedArr = paddedArr;
            this.kernelArr = kernelArr;
            this.convolvedArr = convolvedArr;
            this.startRow = startRow;
            this.endRow = endRow;

            System.out.println("Thread created with startRow: " + startRow + " endRow: " + endRow);
        }

        public void run() {
            // Convolve, ignoring the padded rows
            for (int i = startRow; i < endRow; i++) { // rows
                for (int j = 0; j < convolvedArr[i].length; j++) { // columns
                    int sum = 0;
                    for (int k = 0; k < kernelArr.length; k++) {
                        for (int l = 0; l < kernelArr[k].length; l++) {
                            sum += paddedArr[i + k][j + l] * kernelArr[k][l];
                        }
                    }
                    convolvedArr[i][j] = sum;
                    System.out.printf("Thread %d: Updated C[%d][%d] = %d\n", Thread.currentThread().getId(), i, j, sum);

                    // Sleep for 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Error sleeping");
                        System.exit(1);
                    }
                }
            }
        }
    }

}
