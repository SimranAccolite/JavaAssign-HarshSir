import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Plan implements Comparable<Plan>{
    private int taskId;
    private LocalDate startDate;
    private LocalDate endDate;

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public int compareTo(Plan plan){
        if(this.startDate.compareTo(plan.getStartDate()) != 0)
            return this.startDate.compareTo(plan.getStartDate());
        return this.endDate.compareTo(plan.getEndDate());
    }
}

class GetCancelledDates{
    public void setPlanMap(HashMap<Integer,ArrayList<Plan>> planMap,
                              List<Plan> planList)
    {
        for(Plan plan: planList)
        {
            if(planMap.containsKey(plan.getTaskId())){
                planMap.get(plan.getTaskId()).add(plan);
            }
            else{
                ArrayList<Plan> plans = new ArrayList<>();
                plans.add(plan);
                planMap.put(plan.getTaskId(),plans);
            }
        }
    }

    public void updateRedundantdates(List<Plan> redundantDates, Plan newPlan)
    {
        Collections.sort(redundantDates); //sorting the dates
        int i = 0;

        //If new plan ends before start of old plan
        if(newPlan.getEndDate().compareTo(redundantDates.get(i).getStartDate()) < 0)
            return;

        //Ignoring the non-overlapping old plans
        while(i < redundantDates.size() && redundantDates.get(i).getEndDate().compareTo(newPlan.getStartDate())<0)
            i++;
        if(i == redundantDates.size())
            return;

        //New plan starts before the old plan
        if(newPlan.getStartDate().compareTo(redundantDates.get(i).getStartDate()) <= 0 )
        {
            //New plan covers old plan
            if(newPlan.getEndDate().compareTo(redundantDates.get(i).getEndDate()) >= 0 )
                redundantDates.remove(i);
            else {
                redundantDates.get(i).setStartDate(newPlan.getEndDate().plusDays(1));
                return;
            }
        }

        //Old plan covers new plan
        else if(newPlan.getStartDate().compareTo(redundantDates.get(i).getStartDate()) > 0 &&
                newPlan.getEndDate().compareTo(redundantDates.get(i).getEndDate()) < 0)
        {
            Plan newRedundantDate = new Plan();
            newRedundantDate.setStartDate(newPlan.getEndDate().plusDays(1));
            newRedundantDate.setEndDate(redundantDates.get(i).getEndDate());
            newRedundantDate.setTaskId(redundantDates.get(i).getTaskId());
            redundantDates.add(newRedundantDate);
            redundantDates.get(i).setEndDate(newPlan.getStartDate().minusDays(1));
            return;
        }

        //New plan starts before end of old plan
        else
        {
            redundantDates.get(i).setEndDate(newPlan.getStartDate().minusDays(1));
            i++;
        }

        //Removing all plans covered in new plan
        while(i < redundantDates.size() && newPlan.getEndDate().compareTo(redundantDates.get(i).getEndDate()) >= 0)
            redundantDates.remove(i);
        if(i == redundantDates.size())
            return;

        //New plan ends after starting of old plan
        if(newPlan.getEndDate().compareTo(redundantDates.get(i).getStartDate()) >= 0)
        {
            redundantDates.get(i).setStartDate(newPlan.getEndDate().plusDays(1));
        }
    }


    public List<Plan> getCancelledPeriodsForTask(List<Plan> oldPlanList,
                                                 List<Plan> newPlanList)
    {
        List<Plan> cancelledDates = new ArrayList<Plan>();
        HashMap<Integer,ArrayList<Plan>> newPlanMap = new HashMap<>();
        HashMap<Integer,ArrayList<Plan>> oldPlanMap = new HashMap<>();
        setPlanMap(newPlanMap,newPlanList);
        setPlanMap(oldPlanMap,oldPlanList);

        for(int taskId: oldPlanMap.keySet())
        {
            List<Plan> redundantDates = new ArrayList<Plan>();
            redundantDates.addAll(oldPlanMap.get(taskId));
            for(Plan newPlan: newPlanMap.get(taskId))
            {
                updateRedundantdates(redundantDates,newPlan);
            }
            cancelledDates.addAll(redundantDates);
        }

        return cancelledDates;
    }
}

public class CancelledDates
{
    static Scanner sc;
    static DateTimeFormatter formatter;
    public static void planInput(List<Plan> plans){
        int n = sc.nextInt();
        sc.nextLine();
        for (int i = 0; i < n; i++) {
            String[] inputs = sc.nextLine().split(" ");
            int taskId = Integer.parseInt(inputs[0]);
            String startDate = inputs[1];
            String endDate = inputs[2];
            Plan plan = new Plan();
            plan.setTaskId(taskId);
            plan.setStartDate(LocalDate.parse(startDate,formatter));
            plan.setEndDate(LocalDate.parse(endDate,formatter));
            plans.add(plan);
        }
}

    public static void main(String[] args) {
        formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy");
        sc =  new Scanner(System.in);
        List<Plan> oldPlans = new ArrayList<Plan>();
        System.out.println("***OLD PLAN****");
        planInput(oldPlans);
        List<Plan> newPlans = new ArrayList<Plan>();
        System.out.println("***NEW PLAN****");
        planInput(newPlans);
        GetCancelledDates getCancelledDates = new GetCancelledDates();
        System.out.println("***** Cancelled Dates are:- *****");
        for(Plan plan: getCancelledDates.getCancelledPeriodsForTask(oldPlans,newPlans))
        {
            System.out.println(plan.getTaskId()+"  " +
                    formatter.format(plan.getStartDate()) + "  " +
                    formatter.format(plan.getEndDate()));
        }

    }
}

