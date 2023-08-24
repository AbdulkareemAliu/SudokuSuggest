document.addEventListener("DOMContentLoaded", function() {
    function saveBoard(board){
        localStorage.setItem('boardState', JSON.stringify(board));
    }

    function loadBoard(){
        const boardState = localStorage.getItem('boardState');
        if (boardState) {
            return JSON.parse(boardState)
        } else {
            thisBoard = []
            for (let i = 0; i < 9; i++){
                const row = []
                for (let j = 0; j < 9; j++){
                    row.push({value: ""})
                }
                thisBoard.push([...row])
            }
            return thisBoard;
        }
    }

    const sudokuGrid = document.getElementById("sudoku-grid");
    const boardState = loadBoard();
    console.log(boardState)

    for (let i = 0; i < 9; i++) {
        const row = document.createElement("tr");
        for (let j = 0; j < 9; j++) {
            const cell = document.createElement("td");
            cell.id = i + "" + j;
            cell.contentEditable = true;
            cell.classList.add("editable-cell");
            cell.innerText = boardState[i][j].value
            row.appendChild(cell);
        }
        sudokuGrid.appendChild(row);
    }

    

    // Add event listener to validate user input
    sudokuGrid.addEventListener("input", function(event) {
        const cell = event.target;
        const inputValue = parseInt(cell.innerText);
        
        if (isNaN(inputValue) || inputValue < 1 || inputValue > 9) {
            cell.innerText = "";
        }
        console.log("ID: " + cell.id)
        console.log("Text: " + cell.innerText)
        console.log(boardState[parseInt(cell.id[0])][parseInt(cell.id[1])])
        boardState[parseInt(cell.id[0])][parseInt(cell.id[1])].value = cell.innerText

        saveBoard(boardState);
    });

    const button = document.getElementById("suggest-button");
    const apiResponseTextbox = document.getElementById('apiResponseTextbox');

    button.addEventListener('click', function(){

        fetch('http://localhost:8080/suggest', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                inputBoard: boardState
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
