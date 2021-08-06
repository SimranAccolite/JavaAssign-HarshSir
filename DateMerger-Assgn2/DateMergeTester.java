import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

class DateMerge implements Comparable<DateMerge>{

    public LocalDate startDate;
    public LocalDate endDate;

    public int compareTo(DateMerge dateMerge){
        if(this.startDate.compareTo(dateMerge.startDate) != 0)
            return this.startDate.compareTo(dateMerge.startDate);
        return this.endDate.compareTo(dateMerge.endDate);
    }
}

class DateMerger{
    public ArrayList<DateMerge> mergeDateRange (ArrayList<DateMerge> interval) //[[1,7],[4,9],[10,15]]
    {
        Collections.sort(interval);
        ArrayList<DateMerge> mergedInterval = new ArrayList<>();
        DateMerge start = interval.get(0); //start = [1,7]
        for(DateMerge dateMerge:interval){ //[[1,7],[4,9],[10,15]]
            if (dateMerge.startDate.compareTo(start.endDate) < 0){ //(1,7) (4,7)
                // max(start.get(1), l.get(1));
                start.endDate = start.endDate.compareTo(dateMerge.endDate) <= 0? dateMerge.endDate: start.endDate; //[1,9]
            }
            else
            {
                mergedInterval.add(start);
                start = dateMerge;
            }
        }
        mergedInterval.add(start);
        return mergedInterval;
    }
}

public class DateMergeTester{
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy");
        Scanner sc =  new Scanner(System.in);
        int n = sc.nextInt();
        sc.nextLine();
        ArrayList<DateMerge> intervals = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String[] inputs = sc.nextLine().split(" ");
            String startDate = inputs[0];
            String endDate = inputs[1];
            DateMerge dateMerge = new DateMerge();
            dateMerge.startDate = LocalDate.parse(startDate,formatter);
            dateMerge.endDate = LocalDate.parse(endDate,formatter);
            intervals.add(dateMerge);
        }
        DateMerger dateMerger = new DateMerger();
        System.out.println("***** Merged Interval are: *****");
        System.out.println("START DATE  END DATE");
        for(DateMerge dateMerge: dateMerger.mergeDateRange(intervals))
        {
            System.out.println(formatter.format(dateMerge.startDate) + "  " + formatter.format(dateMerge.endDate));
        }
    }
}


