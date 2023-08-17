document.addEventListener("DOMContentLoaded", function() {
    const sudokuGrid = document.getElementById("sudoku-grid");

    for (let i = 0; i < 9; i++) {
        const row = document.createElement("tr");
        for (let j = 0; j < 9; j++) {
            const cell = document.createElement("td");
            cell.contentEditable = true;
            cell.classList.add("editable-cell");
            row.appendChild(cell);
        }
        sudokuGrid.appendChild(row);
    }

    console.log(sudokuGrid);

    // Add event listener to validate user input
    sudokuGrid.addEventListener("input", function(event) {
        const cell = event.target;
        const inputValue = parseInt(cell.innerText);
        
        if (isNaN(inputValue) || inputValue < 1 || inputValue > 9) {
            cell.innerText = "";
        }
    });

    const button = document.getElementById("suggest-button");
    const apiResponseTextbox = document.getElementById('apiResponseTextbox');

    button.addEventListener('click', function(){
        var board = [];
        for (let i = 0; i < 9; i++) {
            const row = [];
            for (let j = 0; j < 9; j++) {
                const cell = sudokuGrid.rows[i].cells[j];
                const val = cell.innerText;
                row.push({value: val});
            }
            board.push(row);
        }

        fetch('http://localhost:8080/suggest', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                inputBoard: board
            })
        }).then(
            response => response.json())
        .then(data => {
            apiResponseTextbox.textContent = data.response;
            apiResponseTextbox.style.display = 'block'}
        ).catch(error => {
            console.error("Error: ", error)
        })
    
        
        
    })
    
});
