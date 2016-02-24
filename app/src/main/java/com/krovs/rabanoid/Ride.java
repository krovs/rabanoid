package com.krovs.rabanoid;


public class Ride
{
    public String time;
    public String trans;


    public Ride(String time, String train, String bus, String spec, String sat)
    {
        //date format  from database is 00:00:00 so we remove miliseconds
        this.time = time.substring(0, time.length()-3);

        if(train.equals("1") && bus.equals("1"))
            this.trans = "Cercanias / Linea E Aucursa";
        else if(train.equals("1"))
            this.trans = "Cercanias Renfe";
        else if(bus.equals("1"))
        {
            if(spec.equals("1"))
                this.trans = "Especial Aucursa";
            else
                this.trans = "Linea E Aucursa";
        }
    }
}
