/** @jsxImportSource @emotion/react */
import React from 'react'
import { css } from '@emotion/react';

function Footer() {
  return (
    <footer css={footerStyle}></footer>
  )
}

export default Footer

const footerStyle = css`
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-top: 1px solid #e5e7eb;
`;