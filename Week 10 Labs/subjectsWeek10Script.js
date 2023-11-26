function createForm(){

    document.getElementById("instructions").innerHTML=("Please enter your marks for each subject: ");
   
    const numberSubjects=document.getElementById("numberSubjects").valueAsNumber;
    
    for(i=1;i<=numberSubjects;i++){
        document.getElementById("gradeForm").innerHTML+=('<input type=number id="grade'+i+'"><br><br>');
    }
    
    document.getElementById("gradeForm").innerHTML+=('<input type="submit" value="Submit"></form>');
    
    document.getElementById("formDiv").innerHTML=('');
   
}

const subjectForm= document.getElementById("subjectForm");
subjectForm.addEventListener("submit",function(e){
    createForm();
    event.preventDefault();
});

let marksArray=[numberSubjects];

function getMarks(){
    const formLength= document.getElementById("gradeForm").length;
    
    for(i=1;i<formLength;i++){
        let marks=document.getElementById("grade"+i).valueAsNumber;
        marksArray[i-1]= marks;
    }
    
    let sum= 0;
    
    for(i=0;i<marksArray.length;i++){
        
        sum+=marksArray[i];
        
        if(marksArray[i]>80){
            let grade="A";
        }
        else if(marksArray[i]<80 && marksArray[i]>70){
            grade= "B";
        }
        else if(marksArray[i]<70 && marksArray[i]>60){
            grade= "C";
        }
        else if(marksArray[i]<60 && marksArray[i]>50){
            grade="D";
        }
        else{
            grade="F";
        }
        
        document.getElementById("marksPrinted").innerHTML+=("Your marks for Subject "+(i+1)+" are: "+marksArray[i]+"<br>Grade: "+grade+"<br><br>");
    }
    
    document.getElementById("marksPrinted").innerHTML+=("Your overall average is: "+(sum/marksArray.length));
    
    document.getElementById("instructions").innerHTML=(" ");
    
    document.getElementById("formDiv-2").innerHTML=('');
}

const marksForm= document.getElementById("gradeForm");

marksForm.addEventListener("submit", function(e){
    getMarks();
    event.preventDefault();
});

