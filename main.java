import java.lang.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class Tower {
	
	Semaphore towerSem;
	Semaphore mutexPeopleAtTower;
	
	int peopleAtTower = 0;
	
	public Tower(){
		towerSem = new Semaphore(20);
		mutexPeopleAtTower = new Semaphore(1);
		
	}
}

public class Elevator2 {

	Semaphore elevatorSem, WaitingToGoUpSemaphore;
	Semaphore mutexTouristsInside, mutexWizardsInside, mutexPeopleInside, mutexFull, mutexWaitingToGoUp;
	
	int touristsInside = 0;
	int wizardsInside = 0;
	int peopleInside = 0;
	int waitingToGoUp = 0;
	
	boolean full = false;
	
	public Elevator2(){
		elevatorSem = new Semaphore(0);
		WaitingToGoUpSemaphore = new Semaphore(0);
		
		mutexTouristsInside = new Semaphore(1); 
		mutexWizardsInside = new Semaphore(1);
		mutexPeopleInside = new Semaphore(1);
		mutexFull = new Semaphore(1);
		mutexWaitingToGoUp = new Semaphore(1);

	}
	
}


public class Tourist2 extends Person{
	
	public Tourist2(int id, Elevator2 e, Tower t){
		super(id, e, t);
	}
	
	
	public void in_elevador(){
			
		try{
			System.out.println("Tourist " + id + " is waiting to go up.");
			while(true){
					e.mutexWizardsInside.acquire();
					e.mutexPeopleInside.acquire();
					e.mutexTouristsInside.acquire();
					e.mutexFull.acquire();
					
					if(e.peopleInside < 3 && e.full == false) {
						e.touristsInside++;
						e.peopleInside++;
						
						e.mutexFull.release();
						e.mutexTouristsInside.release();
						e.mutexPeopleInside.release();
						e.mutexWizardsInside.release();
						
						e.elevatorSem.acquire();
						
						break;
					}
					if(e.peopleInside == 3 && e.full == false){
						if(e.wizardsInside == 1 && e.touristsInside == 2) {
							System.out.println("There is no place for tourist " + id+ " in this group");
							
							e.mutexFull.release();
							e.mutexTouristsInside.release();
							e.mutexPeopleInside.release();
							e.mutexWizardsInside.release();
							
							break;
						}
						if(e.wizardsInside == 3) {
							System.out.println("There is no place for tourist " + id + " in this group");
							
							e.mutexFull.release();
							e.mutexTouristsInside.release();
							e.mutexPeopleInside.release();
							e.mutexWizardsInside.release();
							
							break;
						}
						if(e.touristsInside == 1 && e.wizardsInside == 2) {
							System.out.println("Tourist " + id + " creates a mixed group.");
							
							e.touristsInside++;
							e.peopleInside++;
							
							e.full = true;
							e.mutexFull.release();
							
							e.mutexTouristsInside.release();
							e.mutexPeopleInside.release();
							e.mutexWizardsInside.release();
							
							e.elevatorSem.release();
							e.elevatorSem.release();
							e.elevatorSem.release();
							
							break;
						}
						if(e.touristsInside == 3) {
							System.out.println("Tourist " + id + " creates a group of tourists.");
							
							e.touristsInside++;
							e.peopleInside++;
							
							e.full = true;
							e.mutexFull.release();
							
							e.mutexTouristsInside.release();
							e.mutexPeopleInside.release();
							e.mutexWizardsInside.release();
							
							e.elevatorSem.release();
							e.elevatorSem.release();
							e.elevatorSem.release();
							
							break;
						}
					}
			}
			
			e.mutexWaitingToGoUp.acquire();
			e.waitingToGoUp++;
			
			System.out.println("Tourist " + id + " is in the elevator.");

			if(e.waitingToGoUp < 4){
				e.mutexWaitingToGoUp.release();
				e.WaitingToGoUpSemaphore.acquire();
			}else{
				e.waitingToGoUp = 0;
				e.mutexWaitingToGoUp.release();
				e.WaitingToGoUpSemaphore.release();
				e.WaitingToGoUpSemaphore.release();
				e.WaitingToGoUpSemaphore.release();
			}
			
			System.out.println("Tourist " + id + " is going up.");
	
		}catch(InterruptedException e){
			System.out.println(e.getMessage());
		}
	}
	

	public void in_tower(){
		
		try{
			t.towerSem.acquire();

			t.mutexPeopleAtTower.acquire();
			t.peopleAtTower++;
			t.mutexPeopleAtTower.release();


			e.mutexPeopleInside.acquire();
			e.mutexTouristsInside.acquire();
			
			e.touristsInside--;
			e.peopleInside--;
			
			if(e.peopleInside == 0){
				System.out.println("Tourist " + id + " enters in the tower and frees the elevator.");
				
				e.mutexTouristsInside.release();
				e.mutexPeopleInside.release();
				
				e.mutexFull.acquire();
				e.full = false;
				e.mutexFull.release();
				
			}else{
				System.out.println("Tourist " + id + " enters in the tower.");
				
				e.mutexTouristsInside.release();
				e.mutexPeopleInside.release();
			}
			
		}catch(InterruptedException e){
			System.out.println(e.getMessage());
		}	
		
	}
	
	public void out_tower(){
		try{
			t.mutexPeopleAtTower.acquire();
			t.peopleAtTower--;
			t.mutexPeopleAtTower.release();
			
			
			t.towerSem.release();
			System.out.println("Tourist " + id + " exits the tower.");
			
		}catch(InterruptedException e){
			System.out.println(e.getMessage());
		}
	}
}

public class Wizard2 extends Person{
	
	public Wizard2(int id, Elevator2 e, Tower t){
		super(id, e, t);
	}
	
	public void in_elevador(){
			
		try{
			System.out.println("Wizard " + id + " is waiting to go up.");
			
			while(true){
					e.mutexWizardsInside.acquire();
					e.mutexPeopleInside.acquire();
					e.mutexTouristsInside.acquire();
					e.mutexFull.acquire();
					
				if(e.peopleInside < 3 && e.full == false) {
					e.wizardsInside++;
					e.peopleInside++;
					
					e.mutexFull.release();
					e.mutexTouristsInside.release();
					e.mutexPeopleInside.release();
					e.mutexWizardsInside.release();
					
					e.elevatorSem.acquire();
					break;
				}
				if(e.peopleInside == 3 && e.full == false){
					if(e.wizardsInside == 2 && e.touristsInside == 1) {
						System.out.println("There is no place for wizard " + id + " in this group");
						
						e.mutexFull.release();
						e.mutexTouristsInside.release();
						e.mutexPeopleInside.release();
						e.mutexWizardsInside.release();
						
						break;
					}
					if(e.touristsInside == 3) {
						System.out.println("There is no place for wizard " + id + " in this group");
						
						e.mutexFull.release();
						e.mutexTouristsInside.release();
						e.mutexPeopleInside.release();
						e.mutexWizardsInside.release();
						
						break;
					}
					if(e.touristsInside == 2 && e.wizardsInside == 1) {
						System.out.println("Wizard " + id + " creates a mixed group.");
						
						e.wizardsInside++;
						e.peopleInside++;
						
						e.full = true;
						e.mutexFull.release();
						
						e.mutexTouristsInside.release();
						e.mutexPeopleInside.release();
						e.mutexWizardsInside.release();
						
						e.elevatorSem.release();
						e.elevatorSem.release();
						e.elevatorSem.release();
						
						break;
					}
					if(e.wizardsInside == 3) {
						System.out.println("Wizard " + id + " creates a group of wizards.");
						
						e.wizardsInside++;
						e.peopleInside++;
						
						e.full = true;
						e.mutexFull.release();
						
						e.mutexTouristsInside.release();
						e.mutexPeopleInside.release();
						e.mutexWizardsInside.release();
						
						e.elevatorSem.release();
						e.elevatorSem.release();
						e.elevatorSem.release();
					
						break;
					}
				}
			}
			
			e.mutexWaitingToGoUp.acquire();
			e.waitingToGoUp++;
			System.out.println("Wizard " + id + " is in the elevator.");

			if(e.waitingToGoUp < 4){
				e.mutexWaitingToGoUp.release();
				e.WaitingToGoUpSemaphore.acquire();
			}else{
				e.waitingToGoUp = 0;

				e.mutexWaitingToGoUp.release();
				e.WaitingToGoUpSemaphore.release();
				e.WaitingToGoUpSemaphore.release();
				e.WaitingToGoUpSemaphore.release();
			}
			
			System.out.println("Wizard " + id + " is going up.");
			
		}catch(InterruptedException e){
			System.out.println(e.getMessage());
		}
	}
	
		

	public void in_tower(){
		
		try{
			//semaphore to enter the tower
			t.towerSem.acquire();

			//mutex sandwich to access the variable peopleAtTower
			t.mutexPeopleAtTower.acquire();
			t.peopleAtTower++;
			t.mutexPeopleAtTower.release();


			//mutex acquire to access the variables of wizards and people
			e.mutexWizardsInside.acquire();
			e.mutexPeopleInside.acquire();
			
			e.wizardsInside--; 
			e.peopleInside--;
			
			if(e.peopleInside == 0){
				
				System.out.println("Wizard " + id + " enters the tower and frees the elevator.");
				
				e.mutexFull.acquire();
				e.full = false;
				e.mutexFull.release();
				
				e.mutexPeopleInside.release();
				e.mutexWizardsInside.release();
				
			}else{
				System.out.println("Wizard " + id + " enters the tower");
				
				e.mutexPeopleInside.release();
				e.mutexWizardsInside.release();
			}
			
		}catch(InterruptedException e){
			System.out.println(e.getMessage());
		}	

	}
	
	public void out_tower(){
		try{
			t.mutexPeopleAtTower.acquire();
			t.peopleAtTower--;
			t.mutexPeopleAtTower.release();
			
			
			t.towerSem.release();
			System.out.println("Wizard " + id + " exits the tower");
			
		}
		catch(InterruptedException e){
			System.out.println(e.getMessage());
		}
	}

}

public abstract class Person extends Thread{

	int id;
	Elevator2 e;
	Tower t;
	
	
	public Person(int id, Elevator2 elevator, Tower tower){
		this.id = id;
		this.e = elevator;
		this.t = tower;

	}
	
	public abstract void in_elevador();
	public abstract void in_tower();
	public abstract void out_tower();
	
	
	Random rnd = new Random();
	
	public void run() {
		
		in_elevador();
		
			try{
				System.out.println("Elevator ascending....");
				Thread.sleep(100);
				System.out.println("Elevator ascending....22222222222222222222");

			}catch(Exception e){};
		
		in_tower();
		
			try{
				Thread.sleep(rnd.nextInt(100)+100);
			}catch(Exception e){};
		
		out_tower();
		
	}
}


public class UniversidadInvisible {
	public static void main(String s[]){
		
		Elevator2 e = new Elevator2();
		Tower tower = new Tower();
		Tourist2 t = null;
		Wizard2 m = null;
		
		final int numWizards = 20;
		final int numTourists = 20;
	
		for(int i = 0; i < numWizards; i++){
			m = new Wizard2(i, e, tower);
			m.start();
		}
		
		for(int i = 0; i < numTourists; i++){
			t = new Tourist2(i, e, tower);
			t.start();
		}
		
		
	}
}
