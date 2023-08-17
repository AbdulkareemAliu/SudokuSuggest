console.log(document.title);

// Select the node that will be observed for mutations
const targetNode = document.body;

// Options for the observer (which mutations to observe)
const config = { attributes: true, childList: true, subtree: true };

// Callback function to execute when mutations are observed
const callback = (mutationList, observer) => {
  for (const mutation of mutationList) {
    
  }
};


// Create an observer instance linked to the callback function
const observer = new MutationObserver(callback);

// Start observing the target node for configured mutations
observer.observe(targetNode, config);


if (document.readyState === "loading") {

    document.addEventListener("DOMContentLoaded", createBoard);

    } else {

        createBoard();

    }


function createBoard(event) {
    console.log("entered create board")
    var board = []

    var rootElement = document.querySelector("html > frameset > frame");
    console.log(rootElement.attributes);
    console.log(rootElement.attributes.getNamedItem("src").value);
    // var gridElement = document.querySelector("#puzzle_grid > tbody"); // Adjust the ID selector as needed
    // console.log(gridElement)

    
    // console.log(gridElement.childNodes);
    // for (var row = 0; row < gridElement.children.length; row++) {
    //     var rowElement = gridElement.children[row];
    //     var curRow = []
    //     for (var col = 0; col < rowElement.children.length; col++) {
    //         var cell = rowElement.children[col];
    //         var id = cell.id;
    //         var colNum = parseInt(id.charAt(1));
    //         var rowNum = parseInt(id.charAt(2));
    //         var inputElement = cell.querySelector('input');
    //         var value = inputElement ? inputElement.value : '.';
    //         curRow.push(value)
    //     }
    //     board.push([...curRow])
    // }

    // Print or process the board
    console.log(board);
}

