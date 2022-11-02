/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;


public class Edificio {
    
    Planta baja=new Planta();
    Planta baja1=new Planta();
    Planta baja2=new Planta();
    int tocaSalir=0;
    
    public void intentarEntrar(int matricula) throws InterruptedException{
        if(baja.entrar(matricula, "0")){
            //Intenta entrar en la planta 0
        }else if(baja1.entrar(matricula, "-1")){
            //Intenta entrar en la planta -1
        }else if(baja2.entrar(matricula, "-2")){
            //Intenta entrar en la planta -2
        }else{
            if(!baja.isCerrado()){
                System.out.println("El vehículo con matrícula "+matricula+" por ho haber plazas");
            }
        }
        aumentarVariableSalir();
        sacarVehiculos();
       
        
        
    }
    public synchronized void  sacarVehiculos(){
        if(tocaSalir==5){
        salir(baja, "planta 0");
        salir(baja1, "planta -1");
        salir(baja2, "planta -2");
        cerrar();
        }
    }
    public void salir(Planta p,String n){
        int i= (int) (Math.random()*4);
        while(i>0){
            p.salir(n);
            i--;
        }
    }
    public void aumentarVariableSalir(){
        if(tocaSalir<5){
            tocaSalir++;
        }else{
            tocaSalir=0;
        }
    }
    public void cerrar(){
        if((int) (Math.random()*100+1)==1){
            if(!baja.isCerrado()){
            baja.setCerrado(true);
            baja1.setCerrado(true);
            baja2.setCerrado(true);
            System.err.println("\n\n\nEl parking ha cerrado por motivos ajenos a la empresa\n\n\n");
            }
        }
        
    }
}
