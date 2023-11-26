function rps(){
    let userGuesses= [5];
    let win= false;
    for(i=0;i<=5;i++){
        let compGuess= Math.floor((Math.random() * 3) + 1);
        let userGuess= prompt("Please guess one of the following: Rock, Paper, Scissors:");
        
        userGuesses[i]= userGuess;

        if(userGuess.toLowerCase()==="rock"){
            if(compGuess===1){
                alert("You chose Rock, the Computer chose Rock \n\nYou draw!");
            }
            else if(compGuess===2){
                alert("You chose Rock, the Computer chose Paper \n\nYou lose!");
            }
            else{
                alert("You chose Rock, the Computer chose Scissors \n\nYou win!");
                win=true;
            }
        }

        else if(userGuess.toLowerCase()==="paper"){
            if(compGuess===1){
                alert("You chose Paper, the Computer chose Rock \n\nYou win!");
                win=true;
            }
            else if(compGuess===2){
                alert("You chose Paper, the Computer chose Paper \n\nYou draw!");
            }
            else{
                alert("You chose Paper, the Computer chose Scissors \n\nYou lose!");
            }
        }

        else if(userGuess.toLowerCase()==="scissors"){
            if(compGuess===1){
                alert("You chose Scissors, the Computer chose Rock \n\nYou lose!");
            }
            else if(compGuess===2){
                alert("You chose Scissors, the Computer chose Paper \n\nYou win!");
                win=true;
            }
            else{
                alert("You chose Scissors, the Computer chose Scissors \n\nYou draw!");
            }
        }

        else{
            alert("Invalid input, please try again!");
        }
        
        if(win===true){
            alert("Congratulations, you won!");
            break;
        }
    }
    
    if (win===false){
        alert("Better luck next time!");
    }
    
    alert("You guessed: "+userGuesses.toString());
}

document.getElementById("playButton").addEventListener("click", function(e){
    rps();
    event.preventDefault();
});