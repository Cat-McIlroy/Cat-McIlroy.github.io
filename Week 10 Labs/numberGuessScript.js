let randNumArray= [];
randNumArray.length= 5;

function fillRandNumArray(){
    
    for(i=0;i<randNumArray.length;i++){
        randNumArray[i]= Math.floor(Math.random()*10)+1;
    }
}

document.getElementById("guessBtn").addEventListener("click",function (e){

fillRandNumArray();
    
let userGuess= parseInt(prompt("Please enter a number between 1 and 10: "));
    
let sum=0;
    
for(i=0;i<randNumArray.length;i++){
    if(randNumArray[i]===userGuess){
        sum+=1;  
    }
}

alert("Numbers: "+randNumArray.toString());
alert("Number of times "+userGuess+" appears in the array: "+sum);
    
});

