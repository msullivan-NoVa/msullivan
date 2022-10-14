import React, {Component, Components} from 'react'
// import styled from 'styled-components'

const PlatformInterigator = (props) => {
//   const Button = styled.button`
//   background-color: black;
//   color: white;
//   font-size: 20px;
//   padding: 10px 60px;
//   border-radius: 5px;
//   margin: 10px 0px;
//   cursor: pointer;
//   &:disabled {
//     color: grey;
//     opacity: 0.7;
//     cursor: default;
//   }
// `;
  return  <div> <button type='input'>Connect {props.selectedPlatform}</button> </div>
}

//  Select also has an AsyncSelect, only loads when interacted with
export default PlatformInterigator