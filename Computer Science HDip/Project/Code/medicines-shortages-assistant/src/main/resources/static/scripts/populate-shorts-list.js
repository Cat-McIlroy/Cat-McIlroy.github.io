document.addEventListener("DOMContentLoaded", async function() {
    
    // clear any previous message
    const messageContainer = document.getElementById("message-container");
    messageContainer.innerHTML = "";
    
    try {
        // fetch data from the backend drugs/shortages API 
        const response = await fetch("http://localhost:8080/drugs/shortages", {
            method: "GET",
            credentials: "include"
        });

        if (response.ok) {
            const drugs = await response.json();

            // get the dropdown element
            const dropdown = document.getElementById("shorts");

            // iterate over the list of drugs and create option for each item in the list
            drugs.forEach(drug => {
                const option = document.createElement("option");
                // the id of the drug is the licenceNo
                option.value = drug.licenceNo; 
                // the text shown in the list should be the productName and manufacturer
                option.textContent = `${drug.productName} - ${drug.manufacturer}`;
                // append item to the dropdown list
                dropdown.appendChild(option);
            });
            
        } else {
            messageContainer.style.display = "flex";
            messageContainer.innerHTML = "Failed to fetch list of drugs shortages.";
            throw new Error("Failed to fetch list of drugs shortages.");
        }
        
    } catch (error) {
        console.error("Error fetching list of drugs shortages:", error);
        messageContainer.style.display = "flex";
        messageContainer.innerHTML = "An error occurred while fetching the list of drugs shortages.";
    }
});
